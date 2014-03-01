/*
 * (C) Copyright 2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     Nuxeo - initial API and implementation
 */

package org.nuxeo.ecm.automation.client.android;

import org.apache.http.params.HttpProtocolParams;
import org.nuxeo.android.cache.blob.BlobStoreManager;
import org.nuxeo.android.cache.sql.SQLStateManager;
import org.nuxeo.android.config.NuxeoServerConfig;
import org.nuxeo.android.documentprovider.AndroidDocumentProvider;
import org.nuxeo.android.documentprovider.DocumentProvider;
import org.nuxeo.android.download.FileDownloader;
import org.nuxeo.android.layout.AndroidLayoutService;
import org.nuxeo.android.layout.NuxeoLayoutService;
import org.nuxeo.android.network.NuxeoNetworkStatus;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.android.upload.FileUploader;
import org.nuxeo.ecm.automation.client.broadcast.DocumentMessageService;
import org.nuxeo.ecm.automation.client.cache.CachedHttpConnector;
import org.nuxeo.ecm.automation.client.cache.DeferredUpdateManager;
import org.nuxeo.ecm.automation.client.cache.OperationType;
import org.nuxeo.ecm.automation.client.cache.ResponseCacheManager;
import org.nuxeo.ecm.automation.client.cache.TransientStateManager;
import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.LoginInfo;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.RequestInterceptor;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpConnector;
import org.nuxeo.ecm.automation.client.jaxrs.impl.NotAvailableOffline;
import org.nuxeo.ecm.automation.client.jaxrs.spi.Connector;
import org.nuxeo.ecm.automation.client.jaxrs.spi.DefaultSession;
import org.nuxeo.ecm.automation.client.jaxrs.spi.auth.BasicAuthInterceptor;

import android.content.Context;
import android.net.http.AndroidHttpClient;

/**
 * See
 * {@link #AndroidAutomationClient(String, Context, SQLStateManager, BlobStoreManager, NuxeoNetworkStatus, NuxeoServerConfig)}
 * about authentication.
 *
 * The {@link #sessionCache} uses {@link NuxeoServerConfig#getCacheKey()} for
 * isolation per URL and Login. If using different authentication methods but
 * want to share the same cache, set another key.
 *
 */
public class AndroidAutomationClient extends HttpAutomationClient {

    private static final String TAG = "AndroidAutomationClient";

    protected final ResponseCacheManager responseCacheManager;

    protected final DeferredUpdateManager deferredUpdatetManager;

    protected final AndroidTransientStateManager transientStateManager;

    protected final NuxeoNetworkStatus networkStatus;

    protected final DocumentMessageService messageHelper;

    protected final SQLStateManager sqlStateManager;

    protected final BlobStoreManager blobStoreManager;

    protected final DocumentProvider documentProvider;

    protected final Context androidContext;

    protected final FileDownloader fileDownloader;

    protected final NuxeoServerConfig serverConfig;

    protected final FileUploader fileUploader;

    protected final NuxeoLayoutService layoutService;

    protected SessionCache sessionCache;

    protected Session currentSession;

    /**
     * Uses by default a {@link BasicAuthInterceptor} with parameters from
     * {@link #serverConfig}. To change authentication mode, set another
     * {@link RequestInterceptor} before calling {@link #getSession()}.
     *
     * @see NuxeoServerConfig
     * @see #setBasicAuth(String, String)
     * @see #setRequestInterceptor(RequestInterceptor)
     *
     */
    public AndroidAutomationClient(String url, Context androidContext,
            SQLStateManager sqlStateManager, BlobStoreManager blobStoreManager,
            NuxeoNetworkStatus offlineSettings, NuxeoServerConfig serverConfig) {
        super(url);
        // Replace HTTP client from mother class
        http.getConnectionManager().shutdown();
        // this.http = new DefaultHttpClient(new ThreadSafeClientConnManager(new
        // BasicHttpParams(), new SchemeRegistry()), new BasicHttpParams());
        this.http = AndroidHttpClient.newInstance("Nuxeo Android Client",
                androidContext);

        // avoid problems when sever returns a http code 100 ...
        HttpProtocolParams.setUseExpectContinue(http.getParams(), false);

        this.sqlStateManager = sqlStateManager;
        this.blobStoreManager = blobStoreManager;
        this.responseCacheManager = new AndroidResponseCacheManager(
                sqlStateManager, blobStoreManager);
        this.deferredUpdatetManager = new AndroidDeferredUpdateManager(
                sqlStateManager);
        this.networkStatus = offlineSettings;
        this.androidContext = androidContext;
        this.messageHelper = new AndroidMessageHelper(androidContext);
        this.transientStateManager = new AndroidTransientStateManager(
                androidContext, sqlStateManager);
        this.documentProvider = new AndroidDocumentProvider(sqlStateManager);
        this.fileDownloader = new FileDownloader(this);
        this.serverConfig = serverConfig;
        this.fileUploader = new FileUploader(this);
        this.layoutService = new AndroidLayoutService(fileDownloader);
        this.sessionCache = new SessionCache();
        setBasicAuth(serverConfig.getLogin(), serverConfig.getPassword());
    }

    public void dropCurrentSession() {
        currentSession = null;
    }

    @Override
    protected Connector newConnector() {
        HttpConnector con = new CachedHttpConnector(http, responseCacheManager,
                networkStatus);
        return con;
    }

    @Override
    public boolean isOffline() {
        return !networkStatus.canUseNetwork();
    }

    @Override
    public String execDeferredUpdate(OperationRequest request,
            AsyncCallback<Object> cb, OperationType opType) {
        if (deferredUpdatetManager != null) {
            boolean executeNow = networkStatus.canUseNetwork();
            return deferredUpdatetManager.execDeferredUpdate(request, cb,
                    opType, executeNow);
        } else {
            throw new UnsupportedOperationException(
                    "No DeferredUpdateManager defined");
        }
    }

    public ResponseCacheManager getResponseCacheManager() {
        return responseCacheManager;
    }

    public DeferredUpdateManager getDeferredUpdatetManager() {
        return deferredUpdatetManager;
    }

    public NuxeoNetworkStatus getNetworkStatus() {
        return networkStatus;
    }

    @Override
    public DocumentMessageService getMessageHelper() {
        return messageHelper;
    }

    public SQLStateManager getSqlStateManager() {
        return sqlStateManager;
    }

    public TransientStateManager getTransientStateManager() {
        return transientStateManager;
    }

    public DocumentProvider getDocumentProvider() {
        return documentProvider;
    }

    public BlobStoreManager getBlobStoreManager() {
        return blobStoreManager;
    }

    public FileDownloader getFileDownloader() {
        return fileDownloader;
    }

    public NuxeoServerConfig getServerConfig() {
        return serverConfig;
    }

    public Context getAndroidContext() {
        return androidContext;
    }

    public FileUploader getFileUploader() {
        return fileUploader;
    }

    public NuxeoLayoutService getLayoutService() {
        return layoutService;
    }

    @Override
    public <T> T getAdapter(Object objToAdapt, Class<T> adapterType) {
        if (adapterType.getName().equals(NuxeoLayoutService.class.getName())) {
            return adapterType.cast(layoutService);
        } else if (adapterType.getName().equals(FileDownloader.class.getName())) {
            return adapterType.cast(fileDownloader);
        } else if (adapterType.getName().equals(FileUploader.class.getName())) {
            return adapterType.cast(fileUploader);
        } else if (adapterType.getName().equals(
                TransientStateManager.class.getName())) {
            return adapterType.cast(transientStateManager);
        } else if (adapterType.getName().equals(
                DeferredUpdateManager.class.getName())) {
            return adapterType.cast(deferredUpdatetManager);
        } else if (adapterType.getName().equals(
                BlobStoreManager.class.getName())) {
            return adapterType.cast(blobStoreManager);
        } else if (adapterType.getName().equals(
                ResponseCacheManager.class.getName())) {
            return adapterType.cast(responseCacheManager);
        } else if (adapterType.getName().equals(
                DocumentMessageService.class.getName())) {
            return adapterType.cast(messageHelper);
        } else if (adapterType.getName().equals(
                DocumentProvider.class.getName())) {
            return adapterType.cast(documentProvider);
        } else if (adapterType.getName().equals(DocumentManager.class.getName())) {
            return adapterType.cast(new DocumentManager((Session) objToAdapt));
        }
        return super.getAdapter(objToAdapt, adapterType);
    }

    /**
     * Given username and password are not used!
     *
     * @return A {@link CachedSession} if available, else a
     *         {@link DefaultSession}
     * @throws NotAvailableOffline If no data in cache and the required online
     *             session fails
     * @deprecated Since 2.0.
     */
    @Deprecated
    @Override
    public Session getSession(final String username, final String password) {
        return getSession();
    }

    /**
     * @return A {@link CachedSession} if available, else a
     *         {@link DefaultSession}
     * @throws NotAvailableOffline If no data in cache and the required online
     *             session fails
     */
    @Override
    public Session getSession() {
        // use the current live session if available
        if (currentSession != null) {
            LoginInfo li = currentSession.getLogin();
            if (li != null && li.getUsername().equals(serverConfig.getLogin())) {
                return currentSession;
            }
        }

        // try to find a cached session
        CachedSession session = sessionCache.getCachedSession(this, url,
                serverConfig.getLogin(), serverConfig.getCacheKey());
        if (session != null) {
            registry = session.getOperationRegistry();
            currentSession = session;

            // try to create a real session in Async mode
            async.submit(new Runnable() {
                @Override
                public void run() {
                    if (networkStatus.isNuxeoServerReachable()) {
                        Session liveSession = createSession();
                        if (liveSession != null) {
                            currentSession = liveSession;
                        }
                    }
                }
            });
            return session;
        }

        // create a real session (require sync call to the server)
        currentSession = createSession();
        if (currentSession != null) {
            sessionCache.storeSession(this, url, serverConfig.getLogin(),
                    serverConfig.getCacheKey());
        }
        return currentSession;
    }

    /**
     * @deprecated Since 2.0. Use {@link #createSession()} instead.
     */
    @Deprecated
    protected Session createSession(String username, String password) {
        return super.getSession(username, password);
    }

    /**
     * @since 2.0
     */
    protected Session createSession() {
        return super.getSession();
    }

    @Override
    public String toString() {
        return "URL=" + url + " network usable="
                + networkStatus.canUseNetwork();
    }

    @Override
    public synchronized void shutdown() {
        super.shutdown();
        if (http != null) {
            ((AndroidHttpClient) http).close();
        }
        try {
            androidContext.unregisterReceiver(transientStateManager);
        } catch (IllegalArgumentException e) {
            // Ignore
        }
    }
}
