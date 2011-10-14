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

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import org.nuxeo.android.adapters.DocumentAttributeResolver;
import org.nuxeo.android.cache.blob.BlobWithProperties;
import org.nuxeo.android.contentprovider.NuxeoContentProviderConfig;
import org.nuxeo.android.layout.LayoutContext;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.WidgetDefinition;
import org.nuxeo.android.upload.FileUploader;
import org.nuxeo.ecm.automation.client.android.UIAsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallbackWithProgress;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class BlobWidgetWrapper extends BaseAndroidWidgetWrapper<PropertyMap> implements AndroidWidgetWrapper {

	protected static final int REQUEST_CODE_BASE = new Random().nextInt(1000);
	protected static final int PICK_IMG = REQUEST_CODE_BASE + 0;
	protected static final int PICK_ANY = REQUEST_CODE_BASE + 1;

	protected int uploadInProgress = 0;
	protected boolean changedValue=false;

	protected LinearLayout layoutWidget;
	protected LinearLayout fileAttributes;
	protected TextView filename;
	protected TextView size;
	protected TextView mimetype;
	protected ProgressBar progressBar;
	protected LinearLayout buttonLayout;
	protected Button uploadImg;
	protected Button uploadFile;
	protected Button openBtn;

	protected AsyncCallbackWithProgress<Serializable> uploadCB;

	@Override
	public boolean validateBeforeModelUpdate() {
		if (uploadInProgress>0) {
			Toast.makeText(getRootContext(),
	                "File upload is still in progress",
	                Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	@Override
	public void updateModel(Document doc) {
		if (mode!=LayoutMode.VIEW && currentValue!=null && changedValue) {
			doc.set(getAttributeName(), currentValue);
		}
	}

	protected void applyBinding() {

		if (currentValue==null) {
			filename.setText("No Blob!");
			size.setVisibility(View.INVISIBLE);
			mimetype.setVisibility(View.INVISIBLE);
		} else {
			filename.setText(currentValue.getString("name"));
			size.setVisibility(View.VISIBLE);
			mimetype.setVisibility(View.VISIBLE);
			size.setText("(" + currentValue.getString("length") + " bytes )");
			mimetype.setText("[" + currentValue.getString("mime-type") + "]");
		}
		if (mode!=LayoutMode.VIEW) {
			progressBar.setVisibility(View.VISIBLE);
		}
		filename.invalidate();
		size.invalidate();
		mimetype.invalidate();
		if (progressBar!=null) {
			progressBar.invalidate();
		}
		layoutWidget.invalidate();
	}

	@Override
	public View buildView(LayoutContext context, LayoutMode mode, Document doc,
			List<String> attributeNames, WidgetDefinition widgetDef) {
		super.buildView(context, mode, doc, attributeNames, widgetDef);

		Context ctx = context.getActivity();
		layoutWidget = new LinearLayout(context.getActivity());
		layoutWidget.setOrientation(LinearLayout.VERTICAL);

		fileAttributes = new LinearLayout(context.getActivity());
		fileAttributes.setOrientation(LinearLayout.HORIZONTAL);

		// Common part
		filename = new TextView(ctx);
		layoutWidget.addView(filename);


		size = new TextView(ctx);
		mimetype = new TextView(ctx);
		fileAttributes.addView(size);
		fileAttributes.addView(mimetype);

		if (mode==LayoutMode.VIEW) {
			openBtn = new Button(layoutWidget.getContext());
			fileAttributes.addView(openBtn);
			openBtn.setBackgroundResource(android.R.drawable.ic_input_get);
			// XXX
			String uriString = "content://" + NuxeoContentProviderConfig.getAuthority() + "/blobs/" + doc.getId() + "/" + getAttributeName();
			final Uri contentUri = Uri.parse(uriString);
			openBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					Intent intent = new Intent(Intent.ACTION_VIEW);
			        intent.setData(contentUri);
			        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			        try {
			        	getHomeActivity().startActivity(intent);
			        }
			        catch (android.content.ActivityNotFoundException e) {
			        	Log.e(BlobWidgetWrapper.class.getSimpleName(), "Unable to start blob viewer",e);
			            Toast.makeText(getRootContext(),
			                "No Application Available to View blob",
			                Toast.LENGTH_SHORT).show();
			        }
				}
			});
		}

		layoutWidget.addView(fileAttributes);

		if (mode!=LayoutMode.VIEW) {
			progressBar = new ProgressBar(ctx, null, android.R.attr.progressBarStyleHorizontal);
			progressBar.setMax(100);
			progressBar.setVisibility(View.INVISIBLE);
			layoutWidget.addView(progressBar);

			buttonLayout = new LinearLayout(layoutWidget.getContext());
			buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
			layoutWidget.addView(buttonLayout);

			uploadImg = new Button(layoutWidget.getContext());
			buttonLayout.addView(uploadImg);
			uploadImg.setBackgroundResource(android.R.drawable.ic_menu_gallery);
			uploadImg.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					progressBar.setVisibility(View.VISIBLE);
					progressBar.invalidate();
					registerActivityResultHandler(PICK_IMG, getHandler(getLayoutContext().getLayoutId()));
					getHomeActivity().startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), PICK_IMG);
				}
			});

			uploadFile = new Button(layoutWidget.getContext());
			buttonLayout.addView(uploadFile);
			uploadFile.setBackgroundResource(android.R.drawable.arrow_up_float);
			uploadFile.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					progressBar.setVisibility(View.VISIBLE);
					progressBar.invalidate();
					registerActivityResultHandler(PICK_ANY, getHandler(getLayoutContext().getLayoutId()));
					Intent intent = new Intent("org.openintents.action.PICK_FILE");
					intent.putExtra("org.openintents.extra.TITLE", "Select a file to attach");
					getHomeActivity().startActivityForResult(intent, PICK_ANY);
					}
			});
		}

		applyBinding();

		uploadCB = new UIAsyncCallback<Serializable>() {

			@Override
			public void onErrorUI(String executionId, Throwable e) {
				//progressBar.setVisibility(View.INVISIBLE);
				//progressBar.invalidate();
				uploadInProgress-=1;
				Toast.makeText(getRootContext(),
		                "File upload failed",
		                Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onProgressUpdate(int progress) {
				progressBar.setProgress(progress);
				progressBar.invalidate();
			}

			@Override
			public void onSuccessUI(String executionId, Serializable data) {
				//progressBar.setVisibility(View.INVISIBLE);
				progressBar.invalidate();
				uploadInProgress-=1;
				Toast.makeText(getRootContext(),
		                "File upload completed",
		                Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onStart() {
				progressBar.setVisibility(View.VISIBLE);
				progressBar.invalidate();
				Toast.makeText(getRootContext(),
		                "File upload started ...",
		                Toast.LENGTH_SHORT).show();
			}
		};


		return layoutWidget;
	}

	@Override
	public void refreshViewFromDocument(Document doc) {
		initCurrentValueFromDocument(doc);
		applyBinding();
	}

	@Override
	protected void initCurrentValueFromDocument(Document doc) {
		Object blobField = DocumentAttributeResolver.get(doc, getAttributeName());
		currentValue = null;
		if (blobField!=null && blobField instanceof PropertyMap) {
			currentValue = (PropertyMap) blobField;
		} else {
			if (blobField!=null) {
				Log.d(this.getClass().getSimpleName(), blobField.toString());
			}
		}
	}


	protected ActivityResultUriToFileHandler getHandler(final String batchId) {

		return new ActivityResultUriToFileHandler(getRootContext()) {

			@Override
			protected void onStreamBlobAvailable(Blob blobToUpload) {

				Log.i(BlobWidgetWrapper.class.getSimpleName(), "Started blob upload with batchId " + batchId);
				BlobWithProperties blobUploading = startUpload(blobToUpload, batchId);

				String uploadUUID = blobUploading.getProperty(FileUploader.UPLOAD_UUID);
				Log.i(BlobWidgetWrapper.class.getSimpleName(), "Started blob upload UUID " + uploadUUID);

				PropertyMap blobProp = new PropertyMap();
				blobProp.set("type", "blob");
				blobProp.set("length",new Long(blobUploading.getLength()));
				blobProp.set("mime-type",blobUploading.getMimeType());
				blobProp.set("name",blobToUpload.getFileName());
				// set information for server side Blob mapping
				blobProp.set("upload-batch",batchId);
				blobProp.set("upload-fileId",blobUploading.getFileName());
				// set information for the update query to know it's dependencies
				blobProp.set("android-require-type", "upload");
				blobProp.set("android-require-uuid", uploadUUID);

				setCurrentValue(blobProp);
				changedValue=true;
				applyBinding();
			}
		};
	}

	protected BlobWithProperties startUpload(Blob blobToUpload, String batchId) {

		BlobWithProperties result = null;

		if (blobToUpload.getFileName()==null) {
			blobToUpload.setFileName(getAttributeName());
		}

		final String fileId = blobToUpload.getFileName();

		FileUploader uploader = getClient().getFileUploader();

		if (getClient().getNetworkStatus().canUseNetwork()) {
			progressBar.setVisibility(View.VISIBLE);
			result = uploader.storeAndUpload(batchId, fileId, blobToUpload, uploadCB);
			uploadInProgress+=1;
		} else {
			Toast.makeText(getRootContext(),
	                "File will be uploaded when network is back",
	                Toast.LENGTH_SHORT).show();
			result = uploader.storeFileForUpload(batchId, fileId, blobToUpload);
		}

		return result;
	}


}
