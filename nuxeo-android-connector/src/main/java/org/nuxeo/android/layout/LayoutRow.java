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

	public List<NuxeoWidget> buildRow(Activity ctx, Document doc, ViewGroup container, Map<String, WidgetDefinition> widgetDefs, LayoutMode mode) {
		ViewGroup rowLayout = createTopLayoutContainer(ctx, container);
		List<NuxeoWidget> widgets = new ArrayList<NuxeoWidget>();
		for (String name : widgetNames) {
			NuxeoWidget widget = widgetDefs.get(name).build(ctx, doc, rowLayout, mode);
			widgets.add(widget);
		}
		return widgets;
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
