package org.nuxeo.android.cache.blob;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import org.nuxeo.ecm.automation.client.cache.StreamHelper;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;

public class BlobStore implements Iterable<Properties>{

	protected static final String NFO_SUFFIX=".info";

	protected final File storageDir;

	public BlobStore(File storageDir) {
		this.storageDir = storageDir;
	}

	public File storeBlob(String key, InputStream is, String fileName, String mimeType) {
		return storeBlob(key, is, fileName, mimeType, null);
	}

	public File storeBlob(String key, InputStream is, String fileName, String mimeType, Properties props) {
		File streamFile = new File(storageDir, key);
		File infoFile = new File(storageDir, key + NFO_SUFFIX);
		if  ( props==null) {
			props = new Properties();
		}
		try {
			FileOutputStream out = new FileOutputStream(streamFile);
			StreamHelper.copy(is, out);
			is.close();
			out.close();
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
		return storeBlob(key, blob, null);
	}
	public  BlobWithProperties storeBlob(String key, Blob blob, Properties props) {
		File file;
		try {
			file = storeBlob(key, blob.getStream(), blob.getFileName(),blob.getMimeType(), props);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new BlobWithProperties(file,blob.getFileName(), blob.getMimeType(), props);

	}
	public boolean hasBlob(String key) {
		return new File(storageDir, key).exists();
	}

	protected BlobWithProperties buildBlob(File file, String key) {
		File fileInfo =  new File(storageDir, key + NFO_SUFFIX );
		String name = key;
		String mimeType="application/octet-stream";
		Properties props = new Properties();
		if (fileInfo.exists()) {
			try {
				props.load(new FileInputStream(fileInfo));
				name = props.getProperty("name", name);
				mimeType = props.getProperty("mimetype", mimeType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new BlobWithProperties(file, name, mimeType, props);
	}

	public BlobWithProperties getBlob(String key) {
		File file =  new File(storageDir, key);
		if (file.exists()) {
			return buildBlob(file, key);
		}
		return null;
	}

	public boolean deleteBlob(String key) {
		boolean result = true;
		File fileToDelete = new File(storageDir, key);
		if (fileToDelete.exists()) {
			result = fileToDelete.delete();
			if (result) {
				fileToDelete = new File(storageDir, key + NFO_SUFFIX);
				if (fileToDelete.exists()) {
					result = fileToDelete.delete();
				}
			}
		}
		return result;
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

	@Override
	public Iterator<Properties> iterator() {

		return new Iterator<Properties>() {

			private int idx=-1;

			private File[] files = storageDir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(NFO_SUFFIX);
				}
			});

			@Override
			public boolean hasNext() {
				return idx < files.length-1;
			}

			@Override
			public Properties next() {
				idx++;
				Properties props = new Properties();
				try {
					props.load(new FileInputStream(files[idx]));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				return props;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}

	public int getCount() {
		return storageDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(NFO_SUFFIX);
			}
		}).length;
	}
}
