package org.nuxeo.android.cache.blob;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.nuxeo.ecm.automation.client.cache.StreamHelper;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;
import org.nuxeo.ecm.automation.client.jaxrs.model.FileBlob;

public class BlobStore {

	protected static final String NFO_SUFFIX=".info";

	protected final File storageDir;

	public BlobStore(File storageDir) {
		this.storageDir = storageDir;
	}

	public File storeBlob(String key, InputStream is, String fileName, String mimeType) {
		File streamFile = new File(storageDir, key);
		File infoFile = new File(storageDir, key + NFO_SUFFIX);
		try {
			FileOutputStream out = new FileOutputStream(streamFile);
			StreamHelper.copy(is, out);
			is.close();
			out.close();
			Properties props = new Properties();
			if (fileName!=null) {
				props.put("filename", fileName);
			}
			if (mimeType!=null) {
				props.put("mimetype", mimeType);
			}
			if (props.size()>0) {
				props.store(new FileOutputStream(infoFile), "Stores meta-infos for " + key);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return streamFile;
	}

	public  Blob storeBlob(String key, Blob blob) {
		File file;
		try {
			file = storeBlob(key, blob.getStream(), blob.getFileName(),blob.getMimeType());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new FileBlob(file,blob.getFileName(), blob.getMimeType());

	}
	public boolean hasBlob(String key) {
		return new File(storageDir, key).exists();
	}

	public File getBlobAsFile(String key) {
		File file =  new File(storageDir, key);
		if (file.exists()) {
			return file;
		}
		return null;
	}

	protected FileBlob buildBlob(File file, String key) {
		File fileInfo =  new File(storageDir, key + NFO_SUFFIX );
		String name = key;
		String mimeType="application/octet-stream";
		if (fileInfo.exists()) {
			Properties props = new Properties();
			try {
				props.load(new FileInputStream(fileInfo));
				name = props.getProperty("name", name);
				mimeType = props.getProperty("mimetype", mimeType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new FileBlob(file, name, mimeType);
	}

	public FileBlob getBlob(String key) {
		File file =  new File(storageDir, key);
		if (file.exists()) {
			return buildBlob(file, key);
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
