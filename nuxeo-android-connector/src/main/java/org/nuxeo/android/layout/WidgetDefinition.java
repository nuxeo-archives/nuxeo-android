package org.nuxeo.android.layout;

import org.nuxeo.android.layout.widgets.AndroidWidgetMapper;
import org.nuxeo.android.layout.widgets.AndroidWidgetWrapper;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WidgetDefinition {

	protected String name;

	protected String type;

	protected String label;

	protected String attributeName;

	protected View view;

	protected LayoutMode mode;

	public WidgetDefinition(String name, String type, String label, String attributeName) {
		this.name=name;
		this.type=type;
		this.label=label;
		this.attributeName=attributeName;
	}

	public String getType() {
		return type;
	}

	public String getLabel() {
		return label;
	}

	public String getName() {
		return name;
	}

	public void build(Context ctx, Document doc, ViewGroup parent, LayoutMode mode) {
		this.mode=mode;
		LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1f);

		if (label!=null) {
			TextView labelW = new TextView(ctx);
			labelW.setText(label + " :");
			labelW.setLayoutParams(params);
			parent.addView(labelW);
		}

		AndroidWidgetWrapper wrapper = AndroidWidgetMapper.getInstance().getWidgetWrapper(type);
		if (wrapper!=null) {
			view = wrapper.build(ctx, mode, doc, attributeName);
			view.setLayoutParams(params);
		}

		if (view!=null) {
			parent.addView(view);
		}
	}

	public void apply(Document doc) {
		if (view!=null) {
			AndroidWidgetWrapper wrapper = AndroidWidgetMapper.getInstance().getWidgetWrapper(type);
			if (wrapper!=null) {
				wrapper.apply(view, mode, doc, attributeName);
			}
		}
	}
}
