package org.nuxeo.android.cache.blob;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.nuxeo.ecm.automation.client.cache.StreamHelper;

public class BlobStore {

	protected final File storageDir;

	public BlobStore(File storageDir) {
		this.storageDir = storageDir;
	}

	public File storeBlob(String key, InputStream is) {
		File streamFile = new File(storageDir, key);
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

	public boolean hasBlob(String key) {
		return new File(storageDir, key).exists();
	}

	public File getBlob(String key) {
		File file =  new File(storageDir, key);
		if (file.exists()) {
			return file;
		}
		return null;
	}

	public boolean deleteBlob(String key) {
		File fileToDelete = new File(storageDir, key);
		if (fileToDelete.exists()) {
			return fileToDelete.delete();
		}
		return true;
	}

	public void clear() {
		for (File file : storageDir.listFiles()) {
			file.delete();
		}
	}

	public long getSize() {
		long size = 0;
		for (File file : storageDir.listFiles()) {
			size+=file.length();
		}
		return size;
	}

}
