/*
 * (C) Copyright 2011-2013 Nuxeo SA (http://nuxeo.com/) and contributors.
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

import java.io.File;
import java.io.IOException;

import org.nuxeo.android.cache.blob.BlobStoreManager;
import org.nuxeo.ecm.automation.client.cache.ResponseCacheEntry;
import org.nuxeo.ecm.automation.client.jaxrs.LoginInfo;
import org.nuxeo.ecm.automation.client.jaxrs.impl.CacheKeyHelper;
import org.nuxeo.ecm.automation.client.jaxrs.spi.JsonMarshalling;
import org.nuxeo.ecm.automation.client.jaxrs.spi.OperationRegistry;
import org.nuxeo.ecm.automation.client.jaxrs.util.IOUtils;

import android.util.Log;

public class SessionCache {

    protected String getCacheKey(String url, String username, String cacheKey) {
        String key = url + ":" + username + ":" + cacheKey;
        String sessionHash = CacheKeyHelper.getHash(key);
        return "session-" + sessionHash;
    }

    public CachedSession getCachedSession(AndroidAutomationClient client,
            String url, String username, String cacheKey) {
        File rootDir = BlobStoreManager.getRootCacheDir(client.androidContext);
        String fileName = getCacheKey(url, username, cacheKey);
        File cache = new File(rootDir, fileName);
        if (cache.exists()) {
            String automationDefKey = CacheKeyHelper.getOperationDefinitionsCacheKey(url);
            ResponseCacheEntry response = client.responseCacheManager.getResponseFromCache(automationDefKey);
            if (response == null) {
                return null;
            }
            try {
                OperationRegistry cachedregistry = JsonMarshalling.readRegistry(IOUtils.read(response.getResponseStream()));
                return new CachedSession(client, cachedregistry, new LoginInfo(
                        username));
            } catch (Exception e) {
                Log.e(this.getClass().getSimpleName(),
                        "Unable to create cached session", e);
            }
        }
        return null;
    }

    public void storeSession(AndroidAutomationClient client, String url,
            String username, String cacheKey) {
        File rootDir = BlobStoreManager.getRootCacheDir(client.androidContext);
        String fileName = getCacheKey(url, username, cacheKey);
        File cache = new File(rootDir, fileName);
        try {
            cache.createNewFile();
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(),
                    "Unable to create Session cache file", e);
            throw new RuntimeException(e);
        }
    }

}
