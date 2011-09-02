package org.nuxeo.ecm.automation.client.cache;

import org.nuxeo.android.network.NuxeoNetworkStatus;
import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpConnector;
import org.nuxeo.ecm.automation.client.jaxrs.spi.Connector;

public class CacheAwareHttpAutomationClient extends HttpAutomationClient {

    protected ResponseCacheManager cacheManager;

    protected DeferredUpdateManager deferredUpdatetManager;

    protected NuxeoNetworkStatus networkStatus;


    public CacheAwareHttpAutomationClient(String url, ResponseCacheManager cacheManager, NuxeoNetworkStatus offlineSettings, DeferredUpdateManager deferredUpdatetManager) {
        super(url);
        this.cacheManager = cacheManager;
        this.networkStatus = offlineSettings;
        this.deferredUpdatetManager = deferredUpdatetManager;
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
			AsyncCallback<Object> cb) {
    	if (deferredUpdatetManager!=null) {
    		boolean executeNow = networkStatus.canUseNetwork();
    		return deferredUpdatetManager.execDeferredUpdate(request, cb, executeNow);
    	} else {
    		throw new UnsupportedOperationException("No DeferredUpdatetManager defined");
    	}
    }

}
