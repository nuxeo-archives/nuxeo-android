package org.nuxeo.android.layout.widgets;

import java.util.Map;

import org.nuxeo.android.layout.ActivityResultHandler;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.NuxeoWidget;
import org.nuxeo.android.layout.WidgetDefinition;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.app.Activity;
import android.view.View;

public interface AndroidWidgetWrapper {

	View build(Activity ctx, LayoutMode mode, Document doc, String attributeName, WidgetDefinition widgetDef);

	void applyChanges(View nativeWidget, LayoutMode mode, Document doc, String attributeName, NuxeoWidget widget);

	void refresh(View nativeWidget, LayoutMode mode, Document doc, String attributeName, NuxeoWidget widget);

	Map<Integer, ActivityResultHandler> getAndFlushPendingActivityResultHandler();

}
