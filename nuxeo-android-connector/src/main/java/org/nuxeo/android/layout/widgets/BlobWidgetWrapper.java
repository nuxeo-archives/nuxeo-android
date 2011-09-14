package org.nuxeo.android.layout.widgets;

import java.util.Random;

import org.nuxeo.android.adapters.DocumentAttributeResolver;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.NuxeoWidget;
import org.nuxeo.android.layout.WidgetDefinition;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BlobWidgetWrapper extends BaseAndroidWidgetWrapper implements AndroidWidgetWrapper {

	protected static final int REQUEST_CODE_BASE = new Random().nextInt(1000);
	protected static final int PICK_IMG = REQUEST_CODE_BASE + 0;
	protected static final int PICK_ANY = REQUEST_CODE_BASE + 1;

	@Override
	public void applyChanges(View nativeWidget, LayoutMode mode, Document doc,
			String attributeName, NuxeoWidget nuxeoWidget) {
		PropertyMap blobProp = getTransientProperty(nativeWidget, doc, attributeName);
		if (blobProp!=null) {
			doc.set(attributeName, blobProp);
		}
	}

	protected PropertyMap getPropertyFromDocument(Document doc, String attributeName) {
		Object blobField = DocumentAttributeResolver.get(doc, attributeName);
		PropertyMap blob = null;
		if (blobField!=null) {
			blob = (PropertyMap) blobField;
		}
		return blob;
	}

	protected PropertyMap getProperty(View view, Document doc, String attributeName) {
		Object transientState = view.getTag();
		if (transientState!=null) {
			return (PropertyMap) transientState;
		}
		return getPropertyFromDocument(doc, attributeName);
	}

	protected PropertyMap getTransientProperty(View view, Document doc, String attributeName) {
		Object transientState = view.getTag();
		if (transientState!=null) {
			return (PropertyMap) transientState;
		}
		return null;
	}



	protected void saveTransientState(View view, Object property) {
		view.setTag(property);
	}

	protected void applyBinding(final LinearLayout widget, LayoutMode mode, final Document doc, String attributeName, WidgetDefinition widgetDef) {

		PropertyMap blob = getProperty(widget, doc, attributeName);

		// flush if needed
		widget.removeAllViewsInLayout();
		if (blob==null) {
			TextView label = new TextView(widget.getContext());
			label.setText("No Blob!");
			widget.addView(label);
		} else {
			TextView filename = new TextView(widget.getContext());
			filename.setText(blob.getString("name"));
			widget.addView(filename);
			TextView size = new TextView(widget.getContext());
			size.setText("(" + blob.getString("length") + " bytes )");
			widget.addView(size);
			TextView mimetype = new TextView(widget.getContext());
			mimetype.setText("[" + blob.getString("mime-type") + "]");
			widget.addView(mimetype);
		}
		if (mode!=LayoutMode.VIEW) {
			LinearLayout buttonLayout = new LinearLayout(widget.getContext());
			buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
			widget.addView(buttonLayout);

			Button uploadImg = new Button(widget.getContext());
			buttonLayout.addView(uploadImg);
			uploadImg.setBackgroundResource(android.R.drawable.ic_menu_gallery);
			uploadImg.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Activity homeActivity = (Activity) widget.getContext();
					registerActivityResultHandler(PICK_IMG, new ActivityResultUriToFileHandler(widget.getContext()) {

						@Override
						protected void onFileAvailable(String key, Blob blobToUpload) {
							PropertyMap blobProp = new PropertyMap();
							blobProp.set("length",new Long(blobToUpload.getLength()));
							blobProp.set("mime-type",blobToUpload.getMimeType());
							blobProp.set("name",key);
							blobProp.set("android-upload-key",key);
							saveTransientState(widget, blobProp);
							//doc.set(key, blobProp); // XXX should not update the model now !!!
							Toast.makeText(widget.getContext(),
						                "File uploading ...",
						                Toast.LENGTH_SHORT).show();
						}
					});
					homeActivity.startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), PICK_IMG);
				}
			});
			Button uploadFile = new Button(widget.getContext());
			buttonLayout.addView(uploadFile);
			uploadFile.setBackgroundResource(android.R.drawable.arrow_up_float);
			uploadFile.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Activity homeActivity = (Activity) widget.getContext();
					registerActivityResultHandler(PICK_ANY, new ActivityResultUriToFileHandler(widget.getContext()) {

						@Override
						protected void onFileAvailable(String key, Blob blobToUpload) {
							PropertyMap blobProp = new PropertyMap();
							blobProp.set("length",new Long(blobToUpload.getLength()));
							blobProp.set("mime-type",blobToUpload.getMimeType());
							blobProp.set("name",key);
							blobProp.set("android-upload-key",key);
							saveTransientState(widget, blobProp);
							//doc.set(key, blobProp);
							Toast.makeText(widget.getContext(),
					                "File uploading ...",
					                Toast.LENGTH_SHORT).show();
						}
					});
					homeActivity.startActivityForResult(new Intent(Intent.ACTION_PICK).setType("*/*"), PICK_ANY);
					}
			});

		}
	}

	@Override
	public View build(Activity ctx, LayoutMode mode, Document doc,
			String attributeName, WidgetDefinition widgetDef) {
		LinearLayout widget = new LinearLayout(ctx);
		widget.setOrientation(LinearLayout.VERTICAL);
		applyBinding(widget, mode, doc, attributeName, widgetDef);
		return widget;
	}

	@Override
	public void refresh(View nativeWidget, LayoutMode mode, Document doc,
			String attributeName, NuxeoWidget nuxeoWidget) {
		applyBinding((LinearLayout)nativeWidget, mode, doc, attributeName, nuxeoWidget.getWidgetDef());
	}

}
