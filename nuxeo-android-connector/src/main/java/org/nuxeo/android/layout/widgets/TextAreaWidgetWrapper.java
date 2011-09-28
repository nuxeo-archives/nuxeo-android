package org.nuxeo.android.layout.widgets;

import org.nuxeo.android.layout.LayoutContext;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.WidgetDefinition;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.text.InputType;
import android.view.View;

public class TextAreaWidgetWrapper extends TextWidgetWrapper {

	@Override
	public View buildView(LayoutContext context, LayoutMode mode, Document doc,
			String attributeName, WidgetDefinition widgetDef) {
		View view =  super.buildView(context, mode, doc, attributeName, widgetDef);

		if (txtWidget!=null) {
			txtWidget.setSingleLine(false);
			txtWidget.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
			txtWidget.setLines(3);
			txtWidget.setMaxLines(3);
		}
		if (editWidget!=null) {
			editWidget.setSingleLine(false);
			editWidget.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
			editWidget.setLines(3);
			editWidget.setMaxLines(3);

		}

		return view;
	}

}
