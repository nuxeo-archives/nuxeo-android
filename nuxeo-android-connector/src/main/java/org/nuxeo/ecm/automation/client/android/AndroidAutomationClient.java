package org.nuxeo.ecm.automation.client.android;

import org.nuxeo.android.cache.sql.SQLStateManager;
import org.nuxeo.android.network.NuxeoNetworkStatus;
import org.nuxeo.ecm.automation.client.broadcast.MessageHelper;
import org.nuxeo.ecm.automation.client.cache.CachedHttpConnector;
import org.nuxeo.ecm.automation.client.cache.OperationType;
import org.nuxeo.ecm.automation.client.cache.DeferredUpdateManager;
import org.nuxeo.ecm.automation.client.cache.ResponseCacheManager;
import org.nuxeo.ecm.automation.client.cache.TransientStateManager;
import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpConnector;
import org.nuxeo.ecm.automation.client.jaxrs.spi.Connector;

import android.content.Context;

public class AndroidAutomationClient extends HttpAutomationClient {

    protected final ResponseCacheManager cacheManager;

    protected final DeferredUpdateManager deferredUpdatetManager;

    protected final TransientStateManager transientStateManager;

    protected final NuxeoNetworkStatus networkStatus;

    protected final MessageHelper messageHelper;

    protected final SQLStateManager sqlStateManager;

    protected final Context androidContext;

    public AndroidAutomationClient(String url, Context androidContext, SQLStateManager sqlStateManager, NuxeoNetworkStatus offlineSettings) {
        super(url);
        this.sqlStateManager=sqlStateManager;
        this.cacheManager = new AndroidResponseCacheManager(androidContext, sqlStateManager);
        this.deferredUpdatetManager = new AndroidDeferedUpdateManager(sqlStateManager);
        this.networkStatus = offlineSettings;
        this.androidContext = androidContext;
        this.messageHelper = new AndroidMessageHelper(androidContext);
        this.transientStateManager = new AndroidTransientStateManager(androidContext, sqlStateManager);
    }

    @Override
    protected Connector newConnector() {
        HttpConnector con =  new CachedHttpConnector(http, cacheManager, networkStatus);
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
		return cacheManager;
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

}
