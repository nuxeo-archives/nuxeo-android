package org.nuxeo.android.layout.widgets;

import java.io.Serializable;
import java.util.Random;

import org.nuxeo.android.adapters.DocumentAttributeResolver;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.WidgetDefinition;
import org.nuxeo.android.upload.FileUploader;
import org.nuxeo.ecm.automation.client.android.UIAsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallbackWithProgress;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

import android.app.Activity;
import android.content.Intent;
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
	protected String uuid;

	protected LinearLayout layoutWidget;
	protected TextView filename;
	protected TextView size;
	protected TextView mimetype;
	protected ProgressBar progressBar;
	protected LinearLayout buttonLayout;
	protected Button uploadImg;
	protected Button uploadFile;

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
		if (mode==LayoutMode.EDIT && currentValue!=null) {
			doc.set(attributeName, currentValue);
		}
	}

	protected void applyBinding() {

		if (currentValue==null) {
			filename.setText("No Blob!");
			size.setVisibility(View.INVISIBLE);
			mimetype.setVisibility(View.INVISIBLE);
		} else {
			Log.i("YOOOOO!!", currentValue.getString("name"));
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
		progressBar.invalidate();
		layoutWidget.invalidate();
	}

	@Override
	public View buildView(Activity ctx, LayoutMode mode, Document doc,
			String attributeName, WidgetDefinition widgetDef) {
		super.buildView(ctx, mode, doc, attributeName, widgetDef);

		layoutWidget = new LinearLayout(ctx);
		layoutWidget.setOrientation(LinearLayout.VERTICAL);

		// Common part
		filename = new TextView(ctx);
		layoutWidget.addView(filename);
		size = new TextView(ctx);
		layoutWidget.addView(size);
		mimetype = new TextView(ctx);
		layoutWidget.addView(mimetype);

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
					registerActivityResultHandler(PICK_IMG, getHandler(uuid));
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
					registerActivityResultHandler(PICK_ANY, getHandler(uuid));
					getHomeActivity().startActivityForResult(new Intent(Intent.ACTION_PICK).setType("*/*"), PICK_ANY);
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
		Object blobField = DocumentAttributeResolver.get(doc, attributeName);
		uuid = doc.getId();
		currentValue = null;
		if (blobField!=null && blobField instanceof PropertyMap) {
			currentValue = (PropertyMap) blobField;
		} else {
			if (blobField!=null) {
				Log.d(this.getClass().getSimpleName(), blobField.toString());
			}
		}
	}


	protected ActivityResultUriToFileHandler getHandler(final String key) {

		return new ActivityResultUriToFileHandler(getRootContext()) {

			@Override
			protected void onStreamBlobAvailable(Blob blobToUpload) {

				blobToUpload = startUpload(blobToUpload, key);

				PropertyMap blobProp = new PropertyMap();
				blobProp.set("length",new Long(blobToUpload.getLength()));
				blobProp.set("mime-type",blobToUpload.getMimeType());
				blobProp.set("name",blobToUpload.getFileName());
				blobProp.set("android-upload-batch",key);
				blobProp.set("android-upload-name",blobToUpload.getFileName());

				setCurrentValue(blobProp);
				applyBinding();
			}
		};
	}

	protected Blob startUpload(Blob blobToUpload, String batchId) {

		Blob result = null;
		final String fileId = getAttributeName();

		blobToUpload.setFileName(fileId);

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