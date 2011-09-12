package org.nuxeo.android.layout.widgets;

import java.util.List;

import org.nuxeo.android.adapters.DocumentAttributeResolver;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.WidgetDefinition;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class SpinnerWidgetWrapper implements AndroidWidgetWrapper {

	@Override
	public void apply(View nativeWidget, LayoutMode mode, Document doc,
			String attributeName, WidgetDefinition widgetDef) {
		if (nativeWidget instanceof Spinner) {
			Spinner spinner = (Spinner) nativeWidget;
			int pos = spinner.getSelectedItemPosition();
			String key = widgetDef.getSelectOptions().getItemValue(pos);
			DocumentAttributeResolver.put(doc, attributeName, key);
		}
	}

	@Override
	public View build(Context ctx, LayoutMode mode, Document doc,
			String attributeName, WidgetDefinition widgetDef) {
		if (mode==LayoutMode.VIEW) {
			TextView widget = new TextView(ctx);
			String value = DocumentAttributeResolver.getString(doc, attributeName);
			widget.setText(widgetDef.getSelectOptions().getLabel(value));
			return widget;
		} else {
			Spinner spinner = new Spinner(ctx);
			spinner.setAdapter(getAdapter(ctx, widgetDef.getSelectOptions().getItemLabels()));
			String value = DocumentAttributeResolver.getString(doc, attributeName);
			int idx = widgetDef.getSelectOptions().getValueIndex(value);
			if (idx>=0) {
				spinner.setSelection(idx);
			}
			return spinner;
		}
	}

	protected SpinnerAdapter getAdapter(Context ctx, List<String> opList) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx,
				android.R.layout.simple_spinner_item, opList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		return adapter;
	}

}
