/*
 * (C) Copyright 2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 */

package org.nuxeo.android.layout.widgets;

import java.util.List;

import org.nuxeo.android.adapters.DocumentAttributeResolver;
import org.nuxeo.android.layout.LayoutContext;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.WidgetDefinition;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyList;

import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class SpinnerMultiWidgetWrapper extends BaseAndroidWidgetWrapper<PropertyList> implements AndroidWidgetWrapper, OnClickListener {

	protected LinearLayout globalContainer;
	protected LinearLayout valueContainer;
	protected LinearLayout spinnerContainer;
	protected TextView textWidget;
	protected Spinner spinner;
	protected Button spinnerButton;


	@Override
	public boolean validateBeforeModelUpdate() {
		return true;
	}

	@Override
	public void updateModel(Document doc) {
		if (mode !=LayoutMode.VIEW) {
			DocumentAttributeResolver.put(doc, getAttributeName(), getCurrentValue());
		}
	}

	@Override
	public void refreshViewFromDocument(Document doc) {
		if (mode==LayoutMode.VIEW) {
			applyBinding();
		} else {
			applyBinding();
		}
	}

	protected void applyBinding() {
		if (mode==LayoutMode.VIEW) {
			StringBuffer sb = new StringBuffer();
			if (getCurrentValue()!=null) {
				for (int i = 0; i < getCurrentValue().size(); i++) {
					sb.append(widgetDef.getSelectOptions().getLabel(getCurrentValue().getString(i)));
					sb.append("\n");
				}
				textWidget.setText(sb.toString());
			}
			return;
		}

		if (getCurrentValue()!=null) {
			valueContainer.removeAllViews();
			for (int i = 0; i < getCurrentValue().size(); i++) {
				TextView txtWidget = new TextView(getHomeActivity());
				txtWidget.setText(widgetDef.getSelectOptions().getLabel(getCurrentValue().getString(i)));
				valueContainer.addView(txtWidget);
			}
		}
	}

	@Override
	protected void initCurrentValueFromDocument(Document doc) {
		Object val = DocumentAttributeResolver.get(doc, getAttributeName());
		if (val instanceof PropertyList) {
			PropertyList value = (PropertyList) val;
			setCurrentValue(value);
		} else {
			if (val==null) {
				Log.w(this.getClass().getSimpleName(), "Init value from doc = null");
			} else {
				Log.w(this.getClass().getSimpleName(), "Init value from doc = " + val.toString());
			}
		}
	}

	@Override
	public View buildView(LayoutContext context, LayoutMode mode, Document doc,
			List<String> attributeNames, WidgetDefinition widgetDef) {
		super.buildView(context, mode, doc, attributeNames, widgetDef);
		Context ctx = context.getActivity();

		LayoutParams paramsW = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);


		if (mode==LayoutMode.VIEW) {
			textWidget = new TextView(ctx);
			textWidget.setSingleLine(false);
			textWidget.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
			textWidget.setLines(3);
			textWidget.setMaxLines(3);
			applyBinding();
			return textWidget;
		} else {
			globalContainer = new LinearLayout(ctx);
			globalContainer.setOrientation(LinearLayout.VERTICAL);

			valueContainer = new LinearLayout(ctx);
			valueContainer.setOrientation(LinearLayout.VERTICAL);


			spinnerContainer = new LinearLayout(ctx);
			spinnerContainer.setOrientation(LinearLayout.HORIZONTAL);
			spinner = new Spinner(ctx);
			spinner.setAdapter(getAdapter(ctx, widgetDef.getSelectOptions().getItemLabels()));
			spinner.setLayoutParams(paramsW);
			spinnerButton = new Button(ctx);
			spinnerButton.setText("Add");
			spinnerButton.setOnClickListener(this);
			spinnerButton.setLayoutParams(paramsW);
			spinnerContainer.addView(spinner);
			spinnerContainer.addView(spinnerButton);

			globalContainer.addView(valueContainer);
			globalContainer.addView(spinnerContainer);
			applyBinding();
			return globalContainer;
		}


	}

	protected SpinnerAdapter getAdapter(Context ctx, List<String> opList) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx,
				android.R.layout.simple_spinner_item, opList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		return adapter;
	}

	@Override
	public void onClick(View arg0) {
		int pos = spinner.getSelectedItemPosition();
		String key = widgetDef.getSelectOptions().getItemValue(pos);
		PropertyList values = getCurrentValue();
		if (values==null) {
			values = new PropertyList();
		}
		values.add(key);
		setCurrentValue(values);
		applyBinding();
	}

}
