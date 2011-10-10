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

import org.nuxeo.android.adapters.DocumentAttributeResolver;
import org.nuxeo.android.layout.LayoutContext;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.WidgetDefinition;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class TextWidgetWrapper extends BaseAndroidWidgetWrapper<String> implements AndroidWidgetWrapper {

	protected TextView txtWidget;
	protected EditText editWidget;

	@Override
	public boolean validateBeforeModelUpdate() {
		return true;
	}

	@Override
	public void updateModel(Document doc) {
		if (editWidget!=null && ! editWidget.getText().toString().equals(getCurrentValue())) {
			DocumentAttributeResolver.put(doc, attributeName, editWidget.getText().toString());
		}
	}

	@Override
	public void refreshViewFromDocument(Document doc) {
		initCurrentValueFromDocument(doc);
		applyBinding();
	}

	protected void applyBinding() {
		if (txtWidget!=null) {
			txtWidget.setText(getCurrentValue());
		}
		if (editWidget!=null) {
			editWidget.setText(getCurrentValue());
		}
	}

	@Override
	public View buildView(LayoutContext context, LayoutMode mode, Document doc,
			String attributeName, WidgetDefinition widgetDef) {

		super.buildView(context, mode, doc, attributeName, widgetDef);

		Context ctx = context.getActivity();

		if (mode==LayoutMode.VIEW) {
			txtWidget = new TextView(ctx);
			applyBinding();
			return txtWidget;
		} else {
			editWidget = new EditText(ctx);
			applyBinding();
			return editWidget;
		}
	}

	@Override
	protected void initCurrentValueFromDocument(Document doc) {
		setCurrentValue(DocumentAttributeResolver.getString(doc, attributeName));
	}

}
