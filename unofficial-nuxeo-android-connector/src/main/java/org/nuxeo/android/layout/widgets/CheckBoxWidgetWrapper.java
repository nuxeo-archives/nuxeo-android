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
import android.widget.CheckBox;

public class CheckBoxWidgetWrapper extends BaseAndroidWidgetWrapper<Boolean>
        implements AndroidWidgetWrapper {

    protected CheckBox checkbox;

    @Override
    protected void initCurrentValueFromDocument(Document doc) {
        Object value = DocumentAttributeResolver.get(doc, getAttributeName());
        Boolean bValue = false;
        if (value != null) {
            if (value instanceof Boolean) {
                bValue = (Boolean) value;
            } else if (value instanceof String) {
                bValue = Boolean.parseBoolean((String) value);
            }
        }
        setCurrentValue(bValue);
    }

    @Override
    public void refreshViewFromDocument(Document doc) {
        initCurrentValueFromDocument(doc);
        applyBinding();
    }

    protected void applyBinding() {
        checkbox.setChecked(getCurrentValue());
        if (LayoutMode.VIEW == mode) {
            checkbox.setEnabled(false);
        }
    }

    @Override
    public View buildView(LayoutContext context, LayoutMode mode, Document doc,
            List<String> attributeNames, WidgetDefinition widgetDef) {
        super.buildView(context, mode, doc, attributeNames, widgetDef);
        Context ctx = context.getActivity();
        checkbox = new CheckBox(ctx);
        return checkbox;
    }

    @Override
    public void updateModel(Document doc) {
        if (mode == LayoutMode.EDIT) {
            doc.set(getAttributeName(),
                    new Boolean(checkbox.isChecked()).toString());
        }
    }

    @Override
    public boolean validateBeforeModelUpdate() {
        return true;
    }

}
