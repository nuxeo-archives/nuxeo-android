package org.nuxeo.android.layout.widgets;

import java.util.Map;

import org.nuxeo.android.layout.ActivityResultHandler;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.WidgetDefinition;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.app.Activity;
import android.view.View;

public interface AndroidWidgetWrapper {

	View buildView(Activity ctx, LayoutMode mode, Document doc, String attributeName, WidgetDefinition widgetDef);

	boolean validateBeforeModelUpdate();

	void updateModel(Document doc);

	void refreshViewFromDocument(Document doc);

	Map<Integer, ActivityResultHandler> getAndFlushPendingActivityResultHandler();

}
