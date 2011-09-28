package org.nuxeo.android.layout;

import org.json.JSONObject;
import org.nuxeo.android.layout.selectOptions.SelectOptions;
import org.nuxeo.android.layout.widgets.AndroidWidgetMapper;
import org.nuxeo.android.layout.widgets.AndroidWidgetWrapper;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

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

	protected JSONObject properties;

	public WidgetDefinition(String name, String type, String label,
			String attributeName, JSONObject properties) {
		this.name = name;
		this.type = type;
		this.label = label;
		this.attributeName = attributeName;
		this.properties = properties;
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

	public NuxeoWidget build(LayoutContext context, Document doc,
			ViewGroup parent, LayoutMode mode) {
		this.mode = mode;

		View view = null;
		LayoutParams paramsL = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1f);
		LayoutParams paramsW = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1f);


		AndroidWidgetWrapper wrapper = AndroidWidgetMapper.getInstance()
				.getWidgetWrapper(this);
		if (wrapper != null) {
			view = wrapper.buildView(context, mode, doc, attributeName, this);
			view.setLayoutParams(paramsW);
		}

		if (view != null) {
			if (label != null) {
				TextView labelW = new TextView(context.getActivity());
				labelW.setText(label + " :");
				labelW.setLayoutParams(paramsL);
				parent.addView(labelW);
			}
			parent.addView(view);
			return new NuxeoWidget(this, view, wrapper);
		}

		return null;
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

	public JSONObject getProperties() {
		if (properties==null) {
			properties = new JSONObject();
		}
		return properties;
	}

}
