package org.nuxeo.android.layout.widgets;

import org.nuxeo.android.adapters.DocumentAttributeResolver;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class TextWidgetWrapper implements AndroidWidgetWrapper {

	@Override
	public void apply(View nativeWidget, LayoutMode mode, Document doc,
			String attributeName) {
		TextView widget = (TextView) nativeWidget;
		DocumentAttributeResolver.put(doc, attributeName, widget.getText());
	}

	@Override
	public View build(Context ctx, LayoutMode mode, Document doc,
			String attributeName) {
		if (mode==LayoutMode.VIEW) {
			TextView widget = new TextView(ctx);
			widget.setText(DocumentAttributeResolver.getString(doc, attributeName));
			return widget;
		} else {
			EditText widget = new EditText(ctx);
			widget.setText(DocumentAttributeResolver.getString(doc, attributeName));
			return widget;
		}
	}

}
