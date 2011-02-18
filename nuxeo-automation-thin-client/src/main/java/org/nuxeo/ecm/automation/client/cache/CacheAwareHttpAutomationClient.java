package org.nuxeo.ecm.automation.client.cache;

import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpConnector;
import org.nuxeo.ecm.automation.client.jaxrs.spi.Connector;

public class CacheAwareHttpAutomationClient extends HttpAutomationClient {

    protected InputStreamCacheManager cacheManager;

    public CacheAwareHttpAutomationClient(String url, InputStreamCacheManager cacheManager) {
        super(url);
        this.cacheManager = cacheManager;
    }

    @Override
    protected Connector newConnector() {
        HttpConnector con =  new HttpConnector(http);
        if (cacheManager!=null) {
            con.setCacheManager(cacheManager);
        }
        return con;
    }
}
