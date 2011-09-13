package org.nuxeo.android.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class LayoutRow {

	List<String> widgetNames = new ArrayList<String>();

	public LayoutRow(List<String> widgetNames) {
		this.widgetNames = widgetNames;
	}

	public void buildRow(Activity ctx, Document doc, ViewGroup container, Map<String, WidgetDefinition> widgetDefs, LayoutMode mode) {
		ViewGroup rowLayout = createTopLayoutContainer(ctx, container);
		for (String name : widgetNames) {
			widgetDefs.get(name).build(ctx, doc, rowLayout, mode);
		}
	}

	public void applyChanges(Document doc,Map<String, WidgetDefinition> widgetDefs) {
		for (String name : widgetNames) {
			widgetDefs.get(name).applyChanges(doc);
		}
	}

	public void refresh(Document doc,Map<String, WidgetDefinition> widgetDefs) {
		for (String name : widgetNames) {
			widgetDefs.get(name).refresh(doc);
		}
	}


	protected ViewGroup createTopLayoutContainer(Context ctx, ViewGroup parent) {
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		container.setLayoutParams(params);
		parent.addView(container);
		return container;
	}


}
