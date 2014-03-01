/*
 * (C) Copyright 2006-2013 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     bstefanescu
 */
package org.nuxeo.ecm.automation.client.jaxrs.spi;

import android.util.Log;

import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.CacheKeyHelper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * @author Tiry
 *
 */
public abstract class AsyncAutomationClient extends AbstractAutomationClient {

    protected static final int NB_THREADS = 4;

    protected static final int QUEUESIZE = 20;

    protected ExecutorService async;

    protected CopyOnWriteArrayList<String> inprogressRequests = new CopyOnWriteArrayList<String>();

    protected ConcurrentHashMap<String, CopyOnWriteArrayList<AsyncCallback<Object>>> pendingCallBacks = new ConcurrentHashMap<String, CopyOnWriteArrayList<AsyncCallback<Object>>>();

    public AsyncAutomationClient(String url) {
        this(url, new ThreadPoolExecutor(0, NB_THREADS, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(QUEUESIZE)));
        // ,new ThreadFactory() {
        // public Thread newThread(Runnable r) {
        // return new Thread("AutomationAsyncExecutor");
        // }
        // }));
    }

    public AsyncAutomationClient(String url, ExecutorService executor) {
        super(url);
        async = executor;
        ((ThreadPoolExecutor) async).prestartAllCoreThreads();
    }

    protected void afterRequestSuccess(String requestKey, Object result) {
        if (pendingCallBacks.containsKey(requestKey)) {
            for (AsyncCallback<Object> cb : pendingCallBacks.get(requestKey)) {
                cb.onSuccess(requestKey, result);
            }
        }
    }

    protected void afterRequestFailure(String requestKey, Throwable t) {
        if (pendingCallBacks.containsKey(requestKey)) {
            for (AsyncCallback<Object> cb : pendingCallBacks.get(requestKey)) {
                cb.onError(requestKey, t);
            }
        }
    }

    @Override
    public String asyncExec(final Session session,
            final OperationRequest request, final AsyncCallback<Object> cb) {

        final String requestKey = CacheKeyHelper.computeRequestKey(request);

        if (inprogressRequests.addIfAbsent(requestKey)) {
            Log.i(AsyncAutomationClient.class.getSimpleName(),
                    "Adding task in the pool");
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    Log.i(AsyncAutomationClient.class.getSimpleName(),
                            "Starting task exec");
                    try {
                        Object result = session.execute(request);
                        cb.onSuccess(requestKey, result);
                        afterRequestSuccess(requestKey, result);
                    } catch (Throwable t) {
                        cb.onError(requestKey, t);
                        afterRequestFailure(requestKey, t);
                    } finally {
                        inprogressRequests.remove(requestKey);
                    }
                }
            };
            async.execute(task);
            Log.i(AsyncAutomationClient.class.getSimpleName(),
                    "New task added to the pool");
        } else {
            Log.i(AsyncAutomationClient.class.getSimpleName(),
                    "Stacking duplicated request");
            CopyOnWriteArrayList<AsyncCallback<Object>> existingQueue = pendingCallBacks.get(requestKey);
            if (existingQueue == null) {
                existingQueue = new CopyOnWriteArrayList<AsyncCallback<Object>>();
            }
            pendingCallBacks.putIfAbsent(requestKey, existingQueue);
            existingQueue.add(cb);
        }
        return requestKey;
    }

    @Override
    public void asyncExec(Runnable runnable) {
        async.execute(runnable);
    }

    @Override
    public synchronized void shutdown() {
        try {
            async.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.e(getClass().getName(), e.getMessage(), e);
        }
        super.shutdown();
        async = null;
    }

    @Override
    public boolean isShutdown() {
        return async == null || async.isShutdown() || super.isShutdown();
    }
}
