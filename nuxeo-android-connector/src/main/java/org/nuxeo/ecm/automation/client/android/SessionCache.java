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

	protected String getCacheKey(String url, String username, String password) {
		String key = url + ":" + username + ":" + password;
		String sessionHash = CacheKeyHelper.getHash(key);
		return "session-"+ sessionHash;
	}

	public CachedSession getCachedSession(AndroidAutomationClient client, String url, String username, String password) {

		File rootDir = BlobStoreManager.getRootCacheDir(client.androidContext);
		String fileName = getCacheKey(url, username, password);
		File cache = new File(rootDir, fileName);
		if (cache.exists()) {
			String automationDefKey = CacheKeyHelper.getOperationDefinitionsCacheKey(url);
			ResponseCacheEntry response = client.responseCacheManager.getResponseFromCache(automationDefKey);

			if (response==null) {
				return null;
			}
			try {
				OperationRegistry cachedregistry = JsonMarshalling.readRegistry(IOUtils.read(response.getResponseStream()));
				return new CachedSession(client, cachedregistry, new LoginInfo(username));
			} catch (Exception e) {
				Log.e(this.getClass().getSimpleName(), "Unable to create cached session", e);
			}
		}
		return null;
	}


	public void storeSession(AndroidAutomationClient client, String url, String username, String password) {
		File rootDir = BlobStoreManager.getRootCacheDir(client.androidContext);
		String fileName = getCacheKey(url, username, password);
		File cache = new File(rootDir, fileName);
		try {
			cache.createNewFile();
		} catch (IOException e) {
			Log.e(this.getClass().getSimpleName(), "Unable to create Session cache file", e);
			throw new RuntimeException(e);
		}
	}

}
