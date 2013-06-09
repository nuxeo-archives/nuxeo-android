/*
 * (C) Copyright 2011-2013 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 */

package org.nuxeo.ecm.automation.client.android;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;
import org.nuxeo.android.cache.sql.DeferedUpdateTableWrapper;
import org.nuxeo.android.cache.sql.SQLStateManager;
import org.nuxeo.ecm.automation.client.broadcast.DocumentMessageService;
import org.nuxeo.ecm.automation.client.broadcast.EventLifeCycle;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.cache.CachedOperationRequest;
import org.nuxeo.ecm.automation.client.cache.DeferredUpdateManager;
import org.nuxeo.ecm.automation.client.cache.OperationType;
import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.Dependency;
import org.nuxeo.ecm.automation.client.jaxrs.Dependency.DependencyType;
import org.nuxeo.ecm.automation.client.jaxrs.ExecutionDependencies;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.CacheKeyHelper;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class AndroidDeferredUpdateManager implements DeferredUpdateManager {

    protected ConcurrentHashMap<String, AsyncCallback<Object>> pendingCallbacks = new ConcurrentHashMap<String, AsyncCallback<Object>>();

    protected final SQLStateManager sqlStateManager;

    public AndroidDeferredUpdateManager(SQLStateManager sqlStateManager) {
        this.sqlStateManager = sqlStateManager;
        sqlStateManager.registerWrapper(new DeferedUpdateTableWrapper());
    }

    protected DeferedUpdateTableWrapper getTableWrapper() {
        return (DeferedUpdateTableWrapper) sqlStateManager.getTableWrapper(DeferedUpdateTableWrapper.TBLNAME);
    }

    @Override
    public void deleteDeferredUpdate(String key) {
        getTableWrapper().deleteEntry(key);
    }

    @Override
    public String execDeferredUpdate(OperationRequest request,
            AsyncCallback<Object> cb, final OperationType opType,
            boolean exeuteNow) {

        final String requestKey = CacheKeyHelper.computeRequestKey(request);
        pendingCallbacks.put(requestKey, cb);

        request = storePendingRequest(requestKey, request, opType);
        final DocumentMessageService messageHelper = request.getSession().getMessageHelper();

        boolean depOk = !request.hasDependencies();
        Log.i(this.getClass().getSimpleName(),
                "Request has dependencies ... checking resolution ");
        if (!depOk) {
            depOk = checkDependencies(request);
        }
        if (depOk) {
            Log.i(this.getClass().getSimpleName(), "Dependencies resolved");
        } else {
            Log.i(this.getClass().getSimpleName(),
                    "There are still pending dependencies, update will have to wait. ");
        }

        if (exeuteNow && depOk) {
            request.execute(new AsyncCallback<Object>() {

                @Override
                public void onError(String executionId, Throwable e) {
                    Log.e(AndroidDeferredUpdateManager.class.getSimpleName(),
                            "Failed to execute deferred op", e);
                    AsyncCallback<Object> clientCB = pendingCallbacks.remove(requestKey);
                    if (clientCB != null) {
                        clientCB.onError(requestKey, e);
                    }
                    // Send Create/Update/Delete after event
                    Bundle extra = new Bundle();
                    extra.putString(
                            NuxeoBroadcastMessages.EXTRA_REQUESTID_PAYLOAD_KEY,
                            executionId);
                    messageHelper.notifyDocumentOperation(null, opType,
                            EventLifeCycle.FAILED, extra);
                }

                @Override
                public void onSuccess(String executionId, Object data) {
                    Log.i(AndroidDeferredUpdateManager.class.getSimpleName(),
                            "Execute deferred op " + executionId);
                    deleteDeferredUpdate(requestKey);
                    AsyncCallback<Object> clientCB = pendingCallbacks.remove(requestKey);
                    // Send Create/Update/Delete after event
                    Document doc = null;
                    if (data != null && data instanceof Document) {
                        doc = (Document) data;
                    }
                    Bundle extra = new Bundle();
                    extra.putString(
                            NuxeoBroadcastMessages.EXTRA_REQUESTID_PAYLOAD_KEY,
                            executionId);
                    messageHelper.notifyDocumentOperation(doc, opType,
                            EventLifeCycle.SERVER, extra);

                    // execute Callback if present
                    if (clientCB != null) {
                        Log.i(AndroidDeferredUpdateManager.class.getSimpleName(),
                                "Call onSuccess client CB");
                        clientCB.onSuccess(requestKey, data);
                    }
                }

            }, CacheBehavior.FORCE_REFRESH);
        }
        return requestKey;
    }

    protected AndroidAutomationClient getClient(Session session) {
        return (AndroidAutomationClient) session.getClient();
    }

    protected boolean checkDependencies(final CachedOperationRequest request) {

        AsyncCallback<Serializable> cb = new AsyncCallback<Serializable>() {
            @Override
            public void onError(String executionId, Throwable e) {
            }

            @Override
            public void onSuccess(String executionId, Serializable data) {
                executePendingRequests(request, null);
            }
        };
        return checkDependencies(getClient(request.getRequest().getSession()),
                request.getRequest().getDependencies(), cb);
    }

    protected boolean checkDependencies(OperationRequest request) {
        return checkDependencies(getClient(request.getSession()),
                request.getDependencies(), null);
    }

    protected boolean checkDependencies(AndroidAutomationClient client,
            ExecutionDependencies dependencies, AsyncCallback<Serializable> cb) {

        Log.i(this.getClass().getSimpleName(),
                "Checking : " + dependencies.size() + " dependencies");
        for (Dependency dep : dependencies) {
            if (dep.getType() == DependencyType.FILE_UPLOAD) {
                Log.i(this.getClass().getSimpleName(), "Found dependency : "
                        + dep.getToken());
                if (client.getFileUploader().isUploadDone(dep.getToken())) {
                    Log.i(this.getClass().getSimpleName(),
                            "Dependency resolved : " + dep.getToken());
                    dependencies.markAsResolved(dep.getToken());
                } else {
                    Log.i(this.getClass().getSimpleName(),
                            "Dependency NOT resolved : " + dep.getToken());
                    if (!client.isOffline() && cb != null) {
                        client.getFileUploader().startUpload(dep.getToken(), cb);
                    }
                }
            }
        }
        return dependencies.resolved();
    }

    protected OperationRequest storePendingRequest(String requestKey,
            OperationRequest request, OperationType opType) {
        return getTableWrapper().storeRequest(requestKey, request, opType);
    }

    protected List<CachedOperationRequest> getPendingRequest(Session session) {
        return getTableWrapper().getPendingRequests(session);
    }

    @Override
    public void executePendingRequests(Session session) {
        executePendingRequests(session, null);
    }

    @Override
    public void executePendingRequests(Session session, final Handler uiNotifier) {
        List<CachedOperationRequest> cachedRequests = getPendingRequest(session);
        executePendingRequests(session, cachedRequests, uiNotifier);
    }

    public void executePendingRequests(CachedOperationRequest cachedRequest,
            final Handler uiNotifier) {
        List<CachedOperationRequest> cachedRequests = new ArrayList<CachedOperationRequest>();
        cachedRequests.add(cachedRequest);
        executePendingRequests(cachedRequest.getRequest().getSession(),
                cachedRequests, uiNotifier);
    }

    public void executePendingRequests(Session session,
            List<CachedOperationRequest> cachedRequests,
            final Handler uiNotifier) {
        final DocumentMessageService messageHelper = session.getMessageHelper();

        for (CachedOperationRequest op : getPendingRequest(session)) {
            OperationRequest request = op.getRequest();
            if (!checkDependencies(op)) {
                Log.i(this.getClass().getSimpleName(),
                        "Skipping operation because dependencies are not resolved");
                continue;
            }
            final String requestKey = op.getOperationKey();
            final OperationType opType = op.getOpType();
            request.execute(new AsyncCallback<Object>() {
                @Override
                public void onError(String executionId, Throwable e) {
                    AsyncCallback<Object> clientCB = pendingCallbacks.remove(requestKey);
                    if (clientCB != null) {
                        clientCB.onError(requestKey, e);
                    }
                    if (uiNotifier != null) {
                        uiNotifier.sendEmptyMessage(0);
                    }
                    // Send Create/Update/Delete after event
                    Bundle extra = new Bundle();
                    extra.putString(
                            NuxeoBroadcastMessages.EXTRA_REQUESTID_PAYLOAD_KEY,
                            executionId);
                    messageHelper.notifyDocumentOperation(null, opType,
                            EventLifeCycle.FAILED, extra);
                }

                @Override
                public void onSuccess(String executionId, Object data) {
                    deleteDeferredUpdate(requestKey);
                    AsyncCallback<Object> clientCB = pendingCallbacks.remove(requestKey);
                    if (clientCB != null) {
                        clientCB.onSuccess(requestKey, data);
                    }
                    if (uiNotifier != null) {
                        uiNotifier.sendEmptyMessage(0);
                    }
                    // Send Create/Update/Delete after event
                    Document doc = null;
                    if (data != null && data instanceof Document) {
                        doc = (Document) data;
                    }
                    Bundle extra = new Bundle();
                    extra.putString(
                            NuxeoBroadcastMessages.EXTRA_REQUESTID_PAYLOAD_KEY,
                            executionId);
                    messageHelper.notifyDocumentOperation(doc, opType,
                            EventLifeCycle.SERVER, extra);
                }
            }, CacheBehavior.FORCE_REFRESH);
        }
    }

    @Override
    public long getPendingRequestCount() {
        return getTableWrapper().getCount();
    }

    @Override
    public void purgePendingUpdates() {
        getTableWrapper().clearTable();
    }
}
