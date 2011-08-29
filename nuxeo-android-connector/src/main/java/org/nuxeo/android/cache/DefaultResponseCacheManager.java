package org.nuxeo.android.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import org.nuxeo.ecm.automation.client.cache.ResponseCacheEntry;
import org.nuxeo.ecm.automation.client.cache.ResponseCacheManager;
import org.nuxeo.ecm.automation.client.cache.StreamHelper;

import android.content.Context;

public class DefaultResponseCacheManager implements ResponseCacheManager {

	protected final SQLCacheHelper sqlHelper;
	protected final File cacheDir;

	public DefaultResponseCacheManager(Context context) {
		sqlHelper = new SQLCacheHelper(context);
		File dir = context.getExternalCacheDir();
		if (dir==null) {
			dir = context.getFilesDir();
		}
		cacheDir =dir;
	}

	@Override
	public InputStream storeResponse(String key, ResponseCacheEntry entry) {

		sqlHelper.storeCacheEntry(key, entry);
		File cachedStream = storeStream(key, entry.getResponseStream());
		try {
			return new FileInputStream(cachedStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected File storeStream(String key, InputStream is) {

		File streamFile = new File(cacheDir, key);
		try {
			FileOutputStream out = new FileOutputStream(streamFile);
			StreamHelper.copy(is, out);
			is.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return streamFile;
	}

	protected InputStream getStream(String key) {
		File streamFile = new File(cacheDir, key);
		try {
			FileInputStream is = new FileInputStream(streamFile);
			return is;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public ResponseCacheEntry getResponseFromCache(String key) {

		ResponseCacheEntry entry = sqlHelper.getEntry(key);
		if (entry!=null) {
			InputStream is = getStream(key);
			if (is==null) {
				return null;
			} else {
				entry.setResponseStream(is);
				return entry;
			}
		}
		return entry;
	}

	@Override
	public long getEntryCount() {
		return sqlHelper.getEntryCount();
	}

	@Override
	public void clear() {
		List<String> keys = sqlHelper.getKeys();
		for (String key : keys) {
			File streamFile = new File(cacheDir, key);
			streamFile.delete();
		}
		sqlHelper.clear();
	}

	@Override
	public long getSize() {
		long size = 0;
		List<String> keys = sqlHelper.getKeys();
		for (String key : keys) {
			File streamFile = new File(cacheDir, key);
			size += streamFile.length();
		}
		return size;
	}
}
