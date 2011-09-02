package org.nuxeo.android.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import org.nuxeo.android.cache.sql.ResponseCacheTableWrapper;
import org.nuxeo.android.cache.sql.SQLStateManager;
import org.nuxeo.ecm.automation.client.cache.ResponseCacheEntry;
import org.nuxeo.ecm.automation.client.cache.ResponseCacheManager;
import org.nuxeo.ecm.automation.client.cache.StreamHelper;

import android.content.Context;
import android.util.Log;

public class DefaultResponseCacheManager implements ResponseCacheManager {

	protected final SQLStateManager sqlStateManager;
	protected final File cacheDir;

	public DefaultResponseCacheManager(Context context, SQLStateManager sqlStateManager) {
		File dir = context.getExternalCacheDir();
		if (dir==null) {
			Log.w(DefaultResponseCacheManager.class.getSimpleName(), "No external directory accessible, using main storage");
			dir = context.getFilesDir();
		}
		cacheDir =dir;
		this.sqlStateManager = sqlStateManager;
		sqlStateManager.registerWrapper(new ResponseCacheTableWrapper());
	}

	protected ResponseCacheTableWrapper getTableWrapper() {
		return (ResponseCacheTableWrapper) sqlStateManager.getTableWrapper(ResponseCacheTableWrapper.TBLNAME);
	}

	@Override
	public InputStream storeResponse(String key, ResponseCacheEntry entry) {

		getTableWrapper().storeCacheEntry(key, entry);
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

		ResponseCacheEntry entry = getTableWrapper().getEntry(key);
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
		return getTableWrapper().getCount();
	}

	@Override
	public void clear() {
		List<String> keys = getTableWrapper().getKeys();
		for (String key : keys) {
			File streamFile = new File(cacheDir, key);
			streamFile.delete();
		}
		getTableWrapper().clearTable();
	}

	@Override
	public long getSize() {
		long size = 0;
		List<String> keys = getTableWrapper().getKeys();
		for (String key : keys) {
			File streamFile = new File(cacheDir, key);
			size += streamFile.length();
		}
		return size;
	}
}
