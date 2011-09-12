package org.nuxeo.android.layout.widgets;

import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.WidgetDefinition;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Context;
import android.view.View;

public interface AndroidWidgetWrapper {

	View build(Context ctx, LayoutMode mode, Document doc, String attributeName, WidgetDefinition widgetDef);

	void apply(View nativeWidget, LayoutMode mode, Document doc, String attributeName, WidgetDefinition widgetDef);


}
