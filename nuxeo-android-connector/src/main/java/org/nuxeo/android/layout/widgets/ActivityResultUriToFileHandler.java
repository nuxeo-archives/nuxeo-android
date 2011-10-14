/*
 * (C) Copyright 2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 */

package org.nuxeo.android.layout.widgets;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;

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

	protected FileNameMap fileNameMap = URLConnection.getFileNameMap();

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
				String fileName = null;

				try {
					afd = context.getContentResolver().openAssetFileDescriptor(dataUri, "r");
					mimeType = context.getContentResolver().getType(dataUri);
					if (dataUri.toString().startsWith("file://")) {
						fileName = dataUri.getLastPathSegment();
					}
					if (mimeType==null && fileName!=null) {
						mimeType = fileNameMap.getContentTypeFor(fileName);
					}
				} catch (FileNotFoundException e) {
					handleError("can not handle uri" + dataUri.toString(), e);
					return false;
				}

				try {
					Blob blob = new StreamBlob(afd.createInputStream(), fileName, mimeType);
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
