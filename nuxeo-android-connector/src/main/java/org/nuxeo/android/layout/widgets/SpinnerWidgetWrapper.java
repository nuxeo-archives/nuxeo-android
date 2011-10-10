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

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class SpinnerWidgetWrapper extends BaseAndroidWidgetWrapper<String> implements AndroidWidgetWrapper {

	protected TextView textWidget;
	protected Spinner spinner;

	@Override
	public boolean validateBeforeModelUpdate() {
		return true;
	}

	@Override
	public void updateModel(Document doc) {
		if ( (mode !=LayoutMode.VIEW)  && spinner!=null) {
			int pos = spinner.getSelectedItemPosition();
			String key = widgetDef.getSelectOptions().getItemValue(pos);
			DocumentAttributeResolver.put(doc, attributeName, key);
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
		if (textWidget!=null) {
			textWidget.setText(widgetDef.getSelectOptions().getLabel(getCurrentValue()));
		} else {
			int idx = widgetDef.getSelectOptions().getValueIndex(getCurrentValue());
			if (idx>=0) {
				spinner.setSelection(idx);
			}
		}
	}

	@Override
	protected void initCurrentValueFromDocument(Document doc) {
		String value = DocumentAttributeResolver.getString(doc, getAttributeName());
		setCurrentValue(value);
	}

	@Override
	public View buildView(LayoutContext context, LayoutMode mode, Document doc,
			String attributeName, WidgetDefinition widgetDef) {
		super.buildView(context, mode, doc, attributeName, widgetDef);

		Context ctx = context.getActivity();
		if (mode==LayoutMode.VIEW) {
			textWidget = new TextView(ctx);
			applyBinding();
			return textWidget;
		} else {
			spinner = new Spinner(ctx);
			spinner.setAdapter(getAdapter(ctx, widgetDef.getSelectOptions().getItemLabels()));
			applyBinding();
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
