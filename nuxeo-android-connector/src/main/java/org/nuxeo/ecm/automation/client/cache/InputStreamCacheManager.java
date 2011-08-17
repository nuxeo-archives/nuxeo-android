package org.nuxeo.ecm.automation.client.cache;

import java.io.InputStream;

public interface InputStreamCacheManager {

    CacheEntry getFromCache(String key);

    InputStream addToCache(String key, CacheEntry entry);

}
