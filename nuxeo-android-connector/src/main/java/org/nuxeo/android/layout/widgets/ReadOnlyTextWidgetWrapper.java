package org.nuxeo.android.layout.widgets;

import org.nuxeo.android.layout.LayoutContext;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.WidgetDefinition;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.view.View;

public class ReadOnlyTextWidgetWrapper extends TextWidgetWrapper {

	@Override
	public void updateModel(Document doc){
		// NOP
	}

	@Override
	public View buildView(LayoutContext context, LayoutMode mode, Document doc,
			String attributeName, WidgetDefinition widgetDef) {
		return super.buildView(context, LayoutMode.VIEW, doc, attributeName, widgetDef);
	}

}
