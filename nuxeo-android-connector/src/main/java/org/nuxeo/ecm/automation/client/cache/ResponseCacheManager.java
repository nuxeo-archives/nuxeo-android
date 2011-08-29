package org.nuxeo.ecm.automation.client.cache;

import java.io.InputStream;

public interface ResponseCacheManager {

	ResponseCacheEntry getResponseFromCache(String key);

    InputStream storeResponse(String key, ResponseCacheEntry entry);

    long getEntryCount();

    void clear();

    long getSize();
}
