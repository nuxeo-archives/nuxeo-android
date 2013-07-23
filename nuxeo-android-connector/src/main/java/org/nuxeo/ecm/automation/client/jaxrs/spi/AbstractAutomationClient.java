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

import static org.nuxeo.ecm.automation.client.jaxrs.Constants.CTYPE_AUTOMATION;
import static org.nuxeo.ecm.automation.client.jaxrs.Constants.CTYPE_ENTITY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.automation.client.broadcast.DocumentMessageService;
import org.nuxeo.ecm.automation.client.cache.OperationType;
import org.nuxeo.ecm.automation.client.jaxrs.AdapterFactory;
import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.AutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.LoginInfo;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.RequestInterceptor;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.spi.auth.BasicAuthInterceptor;

import android.util.Log;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * @author tiry
 */
public abstract class AbstractAutomationClient implements AutomationClient {

    private static final String TAG = "AbstractAutomationClient";

    protected String url;

    protected volatile OperationRegistry registry;

    protected Map<Class<?>, List<AdapterFactory<?>>> adapters;

    protected RequestInterceptor requestInterceptor;

    protected AbstractAutomationClient(String url) {
        this.adapters = new HashMap<Class<?>, List<AdapterFactory<?>>>();
        this.url = url.endsWith("/") ? url : url + "/";
    }

    @Override
    public void setRequestInterceptor(RequestInterceptor interceptor) {
        requestInterceptor = interceptor;
    }

    @Override
    public RequestInterceptor getRequestInterceptor() {
        return requestInterceptor;
    }

    @Override
    public String getBaseUrl() {
        return url;
    }

    @Override
    public void setBasicAuth(String username, String password) {
        setRequestInterceptor(new BasicAuthInterceptor(username, password));
    }

    protected OperationRegistry getRegistry() {
        return registry;
    }

    @Override
    public void registerAdapter(AdapterFactory<?> factory) {
        Class<?> adapter = factory.getAdapterType();
        List<AdapterFactory<?>> factories = adapters.get(adapter);
        if (factories == null) {
            factories = new ArrayList<AdapterFactory<?>>();
            adapters.put(adapter, factories);
        }
        factories.add(factory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAdapter(Object objToAdapt, Class<T> adapterType) {
        Class<?> cls = objToAdapt.getClass();
        List<AdapterFactory<?>> factories = adapters.get(adapterType);
        if (factories != null) {
            for (AdapterFactory<?> f : factories) {
                if (f.getAcceptType().isAssignableFrom(cls)) {
                    return (T) f.getAdapter(objToAdapt);
                }
            }
        }
        return null;
    }

    protected OperationRegistry connect(Connector connector) {
        Log.w(TAG,
                "Using Synch request to init the automation session");
        Log.d(TAG, url);
        Request req = new Request(Request.GET, url);
        req.put("Accept", CTYPE_AUTOMATION);
        // TODO handle authorization failure
        return (OperationRegistry) connector.execute(req);
    }

    @Override
    public synchronized void shutdown() {
        url = null;
        registry = null;
        adapters = null;
    }

    @Override
    public boolean isShutdown() {
        return url == null;
    }

    @Override
    public Session getSession() {
        Connector connector = getConnector();
        if (registry == null) { // not yet connected
            synchronized (this) {
                if (registry == null) {
                    registry = connect(connector);
                }
            }
        }
        return login(connector);
    }

    public Connector getConnector() {
        Connector connector = newConnector();
        if (requestInterceptor != null) {
            connector = new ConnectorHandler(connector, requestInterceptor);
        }
        return connector;
    }

    @Override
    @Deprecated
    public Session getSession(final String username, final String password) {
        setBasicAuth(username, password);
        Session session = null;
        try {
            session = getSession();
        } catch (Throwable t) {
            Log.e(this.getClass().getSimpleName(),
                    "Unable to create live session", t);
        }
        return session;
    }

    @Deprecated
    @Override
    public void getSession(final String username, final String password,
            final AsyncCallback<Session> cb) {
        setBasicAuth(username, password);
        getSession(cb);
    }

    @Override
    public void getSession(final AsyncCallback<Session> cb) {
        asyncExec(new Runnable() {
            @Override
            public void run() {
                try {
                    Session session = getSession();
                    // TODO handle failures
                    cb.onSuccess(null, session);
                } catch (Throwable t) {
                    cb.onError(null, t);
                }
            }
        });
    }

    protected Session login(Connector connector) {
        Request request = new Request(Request.POST, url
                + getRegistry().getPath("login"));
        request.put("Accept", CTYPE_ENTITY);
        LoginInfo login = (LoginInfo) connector.execute(request);
        return createSession(connector, login);
    }

    protected Session createSession(final Connector connector,
            final LoginInfo login) {
        return new DefaultSession(this, connector,
                login == null ? LoginInfo.ANONYNMOUS : login);
    }

    public void asyncExec(Runnable runnable) {
        throw new UnsupportedOperationException("Async execution not supported");
    }

    public String asyncExec(final Session session,
            final OperationRequest request, final AsyncCallback<Object> cb) {
        throw new UnsupportedOperationException("Async execution not supported");
    }

    protected abstract Connector newConnector();

    @Override
    public boolean isOffline() {
        return false;
    }

    public String execDeferredUpdate(OperationRequest request,
            AsyncCallback<Object> cb, OperationType opType) {
        throw new UnsupportedOperationException("No deferred update manager");
    }

    public DocumentMessageService getMessageHelper() {
        throw new UnsupportedOperationException("No MessageHelper");
    }
}
