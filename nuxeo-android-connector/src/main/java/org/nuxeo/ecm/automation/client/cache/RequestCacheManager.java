package org.nuxeo.ecm.automation.client.cache;

import java.io.InputStream;

public interface RequestCacheManager {

	CacheEntry getFromCache(String key);

    InputStream addToCache(String key, CacheEntry entry);

    long getEntryCount();

    void clear();

    long getSize();
}
