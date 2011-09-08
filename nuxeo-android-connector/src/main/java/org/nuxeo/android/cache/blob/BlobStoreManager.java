package org.nuxeo.android.cache.blob;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.automation.client.android.AndroidResponseCacheManager;
import org.nuxeo.ecm.automation.client.cache.StreamHelper;

import android.content.Context;
import android.util.Log;

public class BlobStoreManager {

	protected final File rootDir;

	protected final Map<String, BlobStore> stores = new HashMap<String, BlobStore>();

	public BlobStoreManager(Context context) {

		File dir = context.getExternalCacheDir();
		if (dir==null) {
			Log.w(AndroidResponseCacheManager.class.getSimpleName(), "No external directory accessible, using main storage");
			dir = context.getFilesDir();
		}
		rootDir =dir;
	}

	public BlobStore getBlobStore(String dir) {
		BlobStore store = stores.get(dir);
		if (store==null) {
			File storageDir = getTargetDir(dir);
			store = new BlobStore(storageDir);
			stores.put(dir, store);
		}
		return store;
	}

	protected File getTargetDir(String dir) {
		File targetDir = new File(rootDir,dir);
		if (!targetDir.exists()) {
			targetDir.mkdir();
		}
		return targetDir;
	}












}
