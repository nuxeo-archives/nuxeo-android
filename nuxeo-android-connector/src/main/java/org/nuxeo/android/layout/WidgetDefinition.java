package org.nuxeo.android.layout;

import org.nuxeo.android.layout.selectOptions.SelectOptions;
import org.nuxeo.android.layout.widgets.AndroidWidgetMapper;
import org.nuxeo.android.layout.widgets.AndroidWidgetWrapper;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.app.Activity;
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

	protected LayoutMode mode;

	protected SelectOptions selectOptions;

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

	public NuxeoWidget build(Activity ctx, Document doc, ViewGroup parent, LayoutMode mode) {
		this.mode=mode;

		View view = null;
		LayoutParams paramsL = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);
		LayoutParams paramsW = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);

		if (label!=null) {
			TextView labelW = new TextView(ctx);
			labelW.setText(label + " :");
			labelW.setLayoutParams(paramsL);
			parent.addView(labelW);
		}

		AndroidWidgetWrapper wrapper = AndroidWidgetMapper.getInstance().getWidgetWrapper(type);
		if (wrapper!=null) {
			view = wrapper.build(ctx, mode, doc, attributeName, this);
			view.setLayoutParams(paramsW);
		}

		if (view!=null) {
			parent.addView(view);
		}

		return new NuxeoWidget(this, view);

	}

	public SelectOptions getSelectOptions() {
		return selectOptions;
	}

	public void setSelectOptions(SelectOptions selectOptions) {
		this.selectOptions = selectOptions;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public LayoutMode getMode() {
		return mode;
	}


}
