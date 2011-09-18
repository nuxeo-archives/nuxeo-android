package org.nuxeo.android.layout.widgets;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.nuxeo.android.layout.ActivityResultHandler;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;
import org.nuxeo.ecm.automation.client.jaxrs.model.StreamBlob;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public abstract class ActivityResultUriToFileHandler implements ActivityResultHandler {

	protected Context context;

	public ActivityResultUriToFileHandler(Context context) {
		this.context = context;
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
				} catch (FileNotFoundException e) {
					handleError("can not handle uri" + dataUri.toString(), e);
					return false;
				}

				try {
					Blob blob = new StreamBlob(afd.createInputStream(), null, mimeType);
					onStreamBlobAvailable(blob);
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

	protected abstract void onStreamBlobAvailable(Blob blobToUpload);
}
