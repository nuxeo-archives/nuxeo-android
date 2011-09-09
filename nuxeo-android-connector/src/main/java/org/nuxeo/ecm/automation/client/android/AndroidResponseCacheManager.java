package org.nuxeo.ecm.automation.client.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.nuxeo.android.cache.blob.BlobStore;
import org.nuxeo.android.cache.blob.BlobStoreManager;
import org.nuxeo.android.cache.sql.ResponseCacheTableWrapper;
import org.nuxeo.android.cache.sql.SQLStateManager;
import org.nuxeo.ecm.automation.client.cache.ResponseCacheEntry;
import org.nuxeo.ecm.automation.client.cache.ResponseCacheManager;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;

public class AndroidResponseCacheManager implements ResponseCacheManager {

	protected final SQLStateManager sqlStateManager;
	protected final BlobStore blobStore;

	protected final String BLOBSTORE_KEY = "responses";

	public AndroidResponseCacheManager(SQLStateManager sqlStateManager, BlobStoreManager blobStoreManager) {

		this.sqlStateManager = sqlStateManager;
		this.blobStore = blobStoreManager.getBlobStore(BLOBSTORE_KEY);
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
		return blobStore.storeBlob(key, is, null, null);
	}

	protected InputStream getStream(String key) {
		Blob blob = blobStore.getBlob(key);
		if (blob==null) {
			return null;
		}
		try {
			return blob.getStream();
		} catch (Exception e) {
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
		blobStore.clear();
		getTableWrapper().clearTable();
	}

	@Override
	public long getSize() {
		return blobStore.getSize();
	}
}
