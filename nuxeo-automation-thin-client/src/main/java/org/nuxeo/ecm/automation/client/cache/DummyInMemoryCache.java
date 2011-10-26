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
package org.nuxeo.ecm.automation.client.cache;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class DummyInMemoryCache implements InputStreamCacheManager {

    protected static class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private static final long serialVersionUID = 1L;

        private final int max;

        public LRUCache(int max) {
            super(max, 1.0f, true);
            this.max = max;
        }

        @Override
        protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
            return size() > max;
        }
    }

    protected static Map<String, CacheEntry> cacheEntries = new LRUCache<String, CacheEntry>(
            20);

    @Override
    public InputStream addToCache(String key, CacheEntry entry) {

        cacheEntries.put(key, entry);
        InputStream is = entry.getInputStream();

        byte[] buffer;
        try {
            buffer = StreamHelper.readBytes(is);
            BufferedInputStream bis = new BufferedInputStream(
                    new ByteArrayInputStream(buffer));
            entry.setInputStream(bis);
            return bis;
        } catch (IOException e) {
            throw new RuntimeException("Unable to cache Stream", e);
        }
    }

    @Override
    public CacheEntry getFromCache(String key) {

        CacheEntry entry = cacheEntries.get(key);
        if (entry != null) {
            try {
                entry.getInputStream().reset();
            } catch (IOException e) {
                throw new RuntimeException("Unable to reset Stream", e);
            }
        }
        return entry;
    }

}
