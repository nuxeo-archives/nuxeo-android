package org.nuxeo.ecm.automation.client.cache;

import org.nuxeo.android.network.NuxeoNetworkStatus;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpConnector;
import org.nuxeo.ecm.automation.client.jaxrs.spi.Connector;

public class CacheAwareHttpAutomationClient extends HttpAutomationClient {

    protected RequestCacheManager cacheManager;

    protected NuxeoNetworkStatus offlineSettings;

    public CacheAwareHttpAutomationClient(String url, RequestCacheManager cacheManager, NuxeoNetworkStatus offlineSettings) {
        super(url);
        this.cacheManager = cacheManager;
        this.offlineSettings = offlineSettings;
    }

    @Override
    protected Connector newConnector() {
        HttpConnector con =  new CachedHttpConnector(http, cacheManager, offlineSettings);
        return con;
    }

    public boolean isOffline() {
    	return offlineSettings.isForceOffline() || !offlineSettings.isNetworkReachable();
    }

}
