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

package org.nuxeo.android.layout;

import java.util.List;

import org.json.JSONObject;
import org.nuxeo.android.layout.selectOptions.SelectOptions;
import org.nuxeo.android.layout.widgets.AndroidWidgetMapper;
import org.nuxeo.android.layout.widgets.AndroidWidgetWrapper;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WidgetDefinition {

    protected String name;

    protected String type;

    protected String label;

    protected List<String> attributeNames;

    protected LayoutMode mode;

    protected SelectOptions selectOptions;

    protected JSONObject properties;

    public WidgetDefinition(String name, String type, String label,
            List<String> fieldNames, JSONObject properties) {
        this.name = name;
        this.type = type;
        this.label = label;
        this.attributeNames = fieldNames;
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
        LayoutParams labelLayoutParams = new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1f);
        LayoutParams widgetLayoutParams = new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1f);

        if (LayoutMode.VIEW == mode) {
            labelLayoutParams = new LinearLayout.LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 0.6f);
            widgetLayoutParams = new LinearLayout.LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 0.4f);
        }

        AndroidWidgetWrapper wrapper = AndroidWidgetMapper.getInstance().getWidgetWrapper(
                this);

        if (wrapper != null) {
            view = wrapper.buildView(context, mode, doc, attributeNames, this);
            view.setLayoutParams(widgetLayoutParams);
            view.setPadding(1, 1, 1, 1);
            if (LayoutMode.VIEW == mode) {
                view.setBackgroundColor(Color.rgb(240, 240, 250));
            }
        }

        if (view != null) {
            if (label != null) {
                TextView labelW = new TextView(context.getActivity());
                labelW.setText(label + " :");
                labelW.setTextColor(Color.rgb(80, 80, 80));
                labelW.setLayoutParams(labelLayoutParams);
                parent.addView(labelW);
                if (LayoutMode.VIEW != mode) {
                    labelW.setBackgroundColor(Color.rgb(160, 160, 170));
                    labelW.setTextColor(Color.rgb(20, 20, 40));
                    labelW.setPadding(5, 5, 5, 5);
                }

            }
            parent.addView(view);
            return new NuxeoWidget(this, view, wrapper);
        }

        return null;
    }

    public SelectOptions getSelectOptions() {
        if (selectOptions == null) {
            return new SelectOptions();
        }
        return selectOptions;
    }

    public void setSelectOptions(SelectOptions selectOptions) {
        this.selectOptions = selectOptions;
    }

    public String getAttributeName() {
        return attributeNames.get(0);
    }

    public List<String> getAttributeNames() {
        return attributeNames;
    }

    public LayoutMode getMode() {
        return mode;
    }

    public JSONObject getProperties() {
        if (properties == null) {
            properties = new JSONObject();
        }
        return properties;
    }

}
