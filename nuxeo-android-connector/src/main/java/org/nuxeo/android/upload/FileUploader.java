package org.nuxeo.android.upload;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Properties;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.nuxeo.android.cache.blob.BlobStore;
import org.nuxeo.android.cache.blob.BlobWithProperties;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.android.UIAsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;

import android.util.Log;

public class FileUploader {

	public static final String UPLOAD_UUID = "uuid";
	public static final String FILE_ID = "fileId";
	public static final String BATCH_ID = "batchId";

	protected final BlobStore store;

	protected final AndroidAutomationClient client;

	protected LinkedList<String> uploadDone = new LinkedList<String>();

	public FileUploader(AndroidAutomationClient client) {
		store = client.getBlobStoreManager().getBlobStore("upload");
		this.client = client;
	}

	public BlobWithProperties storeAndUpload(final String batchId, String fileId, final Blob blob, final AsyncCallback<Serializable> cb) {
		final BlobWithProperties res = storeFileForUpload(batchId, fileId, blob);
		startUpload(batchId, fileId, res, cb);
		return res;
	}

	public void startUpload(String key, final AsyncCallback<Serializable> cb) {
		BlobWithProperties blob = store.getBlob(key);
		String batchId = blob.getProperty(BATCH_ID);
		String fileId = blob.getProperty(FILE_ID);
		startUpload(batchId, fileId, blob, cb);
	}

    protected void startUpload(final String batchId, final String fileId, final BlobWithProperties blob, final AsyncCallback<Serializable> cb) {

		client.asyncExec(new Runnable() {

			@Override
			public void run() {

				Log.i(FileUploader.class.getSimpleName(), "Starting upload for Blob with UUID" + blob.getProperty(UPLOAD_UUID));
				String url = client.getServerConfig().getServerBaseUrl() + "site/automation/batch/upload";
				HttpPost post = new HttpPost(url);
				post.setHeader("Cache-Control", "no-cache");
				post.setHeader("X-File-Name", blob.getFileName());
				post.setHeader("X-File-Size", blob.getLength()+"");
				post.setHeader("X-File-Type", blob.getMimeType());
				post.setHeader("X-Batch-Id", batchId);
				post.setHeader("X-File-Idx", fileId);

				try {

					HttpEntity blobEntity = null;

					if (cb!=null && cb instanceof UIAsyncCallback<?>) {
						blobEntity = new RepeatableBlobEntityWithProgress(blob, (UIAsyncCallback<Serializable>)cb);
						((UIAsyncCallback<Serializable>)cb).notifyStart();
					} else {
						blobEntity = new RepeatableBlobEntityWithProgress(blob, null);
					}

					post.setEntity(blobEntity);

					HttpResponse response = client.getConnector().executeSimpleHttp(post);
					if (response.getStatusLine().getStatusCode()==200) {
						Log.i(FileUploader.class.getSimpleName(), "Upload completed successfuly for Blob with UUID" + blob.getProperty(UPLOAD_UUID));
						if (cb!=null) {
							cb.onSuccess(batchId, response.getStatusLine().getReasonPhrase());
						}
						removeBlob(blob);
					} else {
						if (cb!=null) {
							cb.onError(batchId, new Exception("Server returned status code " + response.getStatusLine().getStatusCode()));
						}
						Log.e(FileUploader.class.getSimpleName(), "Server returned status code " + response.getStatusLine().getStatusCode());
					}

				} catch (Exception e) {
					if (cb!=null) {
						cb.onError(batchId, e);
					}
					Log.e(FileUploader.class.getSimpleName(), "Exception during upload", e);
				}
			}
		});

	}


	public BlobWithProperties storeFileForUpload(String batchId, String fileId, Blob blob) {

		String key=UUID.randomUUID().toString();
		Properties props = new Properties();
		props.put(BATCH_ID, batchId);
		props.put(FILE_ID, fileId);
		props.put(UPLOAD_UUID, key);
		return store.storeBlob(key, blob, props);
	}

	protected void removeBlob(BlobWithProperties blob) {
		store.deleteBlob(blob.getProperty(UPLOAD_UUID));
	}

	public void cancelUpload(String batchId) {
		// XXX
	}

	public boolean isUploadDone(String key) {
		if (uploadDone.contains(key)) {
			return true;
		}

		for (Properties props : store) {
			String uuid = props.getProperty(UPLOAD_UUID);
			if (uuid!=null && uuid.equals(key)) {
				return false;
			}
		}

		uploadDone.addLast(key);
		if (uploadDone.size()>20) {
			uploadDone.removeFirst();
		}
		return true;
	}
}
