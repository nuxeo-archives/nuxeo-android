package org.nuxeo.android.layout.widgets;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import org.nuxeo.android.cache.blob.BlobStore;
import org.nuxeo.android.cache.blob.BlobStoreManager;
import org.nuxeo.android.layout.ActivityResultHandler;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public abstract class ActivityResultUriToFileHandler implements ActivityResultHandler {

	protected Context context;

	protected BlobStore store;

	public ActivityResultUriToFileHandler(Context context) {
		this.context = context;
	}

	public BlobStore getBlobStore() {
		if (store==null) {
			store = new BlobStoreManager(context).getBlobStore("upload");
		}
		return store;
	}

	@Override
	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			Uri dataUri = data.getData();
			if (dataUri!=null) {
				AssetFileDescriptor afd = null;
				String mimeType = null;
				try {
					afd = context.getContentResolver().openAssetFileDescriptor(dataUri, "r");
					mimeType = context.getContentResolver().getType(dataUri);
					Bundle extra = data.getExtras();
				} catch (FileNotFoundException e) {
					handleError("can not handle uri" + dataUri.toString(), e);
					return false;
				}

				String key = UUID.randomUUID().toString();
				String fileName = "upload-" + key;

				try {
					getBlobStore().storeBlob(key, afd.createInputStream(), fileName, mimeType);
					onFileAvailable(key, getBlobStore().getBlob(key));
				} catch (IOException e) {
					handleError("Can not read the stream" + dataUri.toString(), e);
					return false;
				}
				return true;
			}
		}
		return false;
	}

	protected void handleError(String message, Exception e) {
		Log.e(this.getClass().getSimpleName(), message, e);
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	protected abstract void onFileAvailable(String key, Blob blobToUpload);
}
