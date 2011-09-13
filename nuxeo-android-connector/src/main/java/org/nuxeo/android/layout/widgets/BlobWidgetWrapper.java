package org.nuxeo.android.layout.widgets;

import org.nuxeo.android.adapters.DocumentAttributeResolver;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.WidgetDefinition;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BlobWidgetWrapper implements AndroidWidgetWrapper {

	@Override
	public void applyChanges(View nativeWidget, LayoutMode mode, Document doc,
			String attributeName, WidgetDefinition widgetDef) {
	}

	protected void applyBinding(final LinearLayout widget, LayoutMode mode, Document doc, String attributeName, WidgetDefinition widgetDef) {

		Object blobField = DocumentAttributeResolver.get(doc, attributeName);
		PropertyMap blob = null;
		if (blobField!=null) {
			blob = (PropertyMap) blobField;
		}
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
				public void onClick(View arg0) {
					Activity homeActivity = (Activity) widget.getContext();
					homeActivity.startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), 0);
				}
			});
			Button uploadFile = new Button(widget.getContext());
			buttonLayout.addView(uploadFile);
			uploadFile.setBackgroundResource(android.R.drawable.arrow_up_float);
			uploadFile.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Activity homeActivity = (Activity) widget.getContext();
					homeActivity.startActivityForResult(new Intent(Intent.ACTION_PICK).setType("*/*"), 1);

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
			String attributeName, WidgetDefinition widgetDef) {
		applyBinding((LinearLayout)nativeWidget, mode, doc, attributeName, widgetDef);
	}

}
