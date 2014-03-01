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
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class TextAreaWidgetWrapper extends BaseAndroidWidgetWrapper<String>
        implements AndroidWidgetWrapper {

    protected TextView txtWidget;

    protected EditText editWidget;

    @Override
    public boolean validateBeforeModelUpdate() {
        return true;
    }

    @Override
    public void updateModel(Document doc) {
        if (editWidget != null
                && !editWidget.getText().toString().equals(getCurrentValue())) {
        	
        	String value = editWidget.getText().toString();
            DocumentAttributeResolver.put(doc, getAttributeName(),
            		value);
        }
    }

    @Override
    public void refreshViewFromDocument(Document doc) {
        initCurrentValueFromDocument(doc);
        applyBinding();
    }

    protected void applyBinding() {
        if (txtWidget != null) {
            txtWidget.setText(getCurrentValue());
        }
        if (editWidget != null) {
            editWidget.setText(getCurrentValue());
        }
    }

    @Override
    public View buildView(LayoutContext context, LayoutMode mode, Document doc,
            List<String> attributeNames, WidgetDefinition widgetDef) {

        super.buildView(context, mode, doc, attributeNames, widgetDef);

        Context ctx = context.getActivity();

        View view = null;
        if (mode == LayoutMode.VIEW) {
            txtWidget = new TextView(ctx);
            txtWidget.setSingleLine(false);
            txtWidget.setLines(3);
            txtWidget.setGravity(Gravity.TOP | Gravity.LEFT);
            view = txtWidget;
        } else {
            editWidget = new EditText(ctx);
            editWidget.setSingleLine(false);
            editWidget.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            editWidget.setLines(3);
            editWidget.setGravity(Gravity.TOP | Gravity.LEFT);
            view = editWidget;
        }
        applyBinding();

        return view;
    }

    @Override
    protected void initCurrentValueFromDocument(Document doc) {
        setCurrentValue(DocumentAttributeResolver.getString(doc,
                getAttributeName()));
    }

}
