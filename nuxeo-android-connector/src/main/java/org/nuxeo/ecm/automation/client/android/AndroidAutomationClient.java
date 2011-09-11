package org.nuxeo.ecm.automation.client.android;

import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.nuxeo.android.cache.blob.BlobStoreManager;
import org.nuxeo.android.cache.sql.SQLStateManager;
import org.nuxeo.android.config.NuxeoServerConfig;
import org.nuxeo.android.documentprovider.AndroidDocumentProvider;
import org.nuxeo.android.documentprovider.DocumentProvider;
import org.nuxeo.android.download.FileDownloader;
import org.nuxeo.android.network.NuxeoNetworkStatus;
import org.nuxeo.ecm.automation.client.broadcast.MessageHelper;
import org.nuxeo.ecm.automation.client.cache.CachedHttpConnector;
import org.nuxeo.ecm.automation.client.cache.DeferredUpdateManager;
import org.nuxeo.ecm.automation.client.cache.OperationType;
import org.nuxeo.ecm.automation.client.cache.ResponseCacheManager;
import org.nuxeo.ecm.automation.client.cache.TransientStateManager;
import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpConnector;
import org.nuxeo.ecm.automation.client.jaxrs.spi.Connector;

import android.content.Context;
import android.net.http.AndroidHttpClient;

public class AndroidAutomationClient extends HttpAutomationClient {

    protected final ResponseCacheManager responseCacheManager;

    protected final DeferredUpdateManager deferredUpdatetManager;

    protected final TransientStateManager transientStateManager;

    protected final NuxeoNetworkStatus networkStatus;

    protected final MessageHelper messageHelper;

    protected final SQLStateManager sqlStateManager;

    protected final BlobStoreManager blobStoreManager;

    protected final DocumentProvider documentProvider;

    protected final Context androidContext;

    protected final FileDownloader fileDownloader;

    protected final NuxeoServerConfig serverConfig;

    public AndroidAutomationClient(String url, Context androidContext, SQLStateManager sqlStateManager, BlobStoreManager blobStoreManager, NuxeoNetworkStatus offlineSettings, NuxeoServerConfig serverConfig) {
        super(url);
        //this.http = new DefaultHttpClient(new ThreadSafeClientConnManager(new BasicHttpParams(), new SchemeRegistry()), new BasicHttpParams());
        this.http = AndroidHttpClient.newInstance("Nuxeo Android Client", null);
        // avoid problems when sever returns a http code 100 ...
        HttpProtocolParams.setUseExpectContinue(http.getParams(), false);

        this.sqlStateManager=sqlStateManager;
        this.blobStoreManager=blobStoreManager;
        this.responseCacheManager = new AndroidResponseCacheManager(sqlStateManager,blobStoreManager);
        this.deferredUpdatetManager = new AndroidDeferedUpdateManager(sqlStateManager);
        this.networkStatus = offlineSettings;
        this.androidContext = androidContext;
        this.messageHelper = new AndroidMessageHelper(androidContext);
        this.transientStateManager = new AndroidTransientStateManager(androidContext, sqlStateManager);
        this.documentProvider = new AndroidDocumentProvider(sqlStateManager);
        this.fileDownloader = new FileDownloader(this);
        this.serverConfig=serverConfig;
    }

    @Override
    protected Connector newConnector() {
        HttpConnector con =  new CachedHttpConnector(http, responseCacheManager, networkStatus);
        return con;
    }

    public boolean isOffline() {
    	return !networkStatus.canUseNetwork();
    }

    public String execDeferredUpdate(OperationRequest request,
			AsyncCallback<Object> cb, OperationType opType) {
    	if (deferredUpdatetManager!=null) {
    		boolean executeNow = networkStatus.canUseNetwork();
    		return deferredUpdatetManager.execDeferredUpdate(request, cb, opType, executeNow);
    	} else {
    		throw new UnsupportedOperationException("No DeferredUpdatetManager defined");
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

	public MessageHelper getMessageHelper() {
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
}
