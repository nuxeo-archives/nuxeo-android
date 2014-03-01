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

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.android.layout.LayoutContext;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.WidgetDefinition;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Context;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RichTextWidgetWrapper extends
        BaseAndroidWidgetWrapper<List<String>> implements AndroidWidgetWrapper {

    protected TextView txtWidget;

    protected EditText editWidget;

    protected WebView htmlWidget;

    protected LinearLayout container;

    @Override
    protected void initCurrentValueFromDocument(Document doc) {
        List<String> fields = new ArrayList<String>();
        fields.add(doc.getString(getAttributeNames().get(0), ""));
        fields.add(doc.getString(getAttributeNames().get(1), "text/plain"));
        setCurrentValue(fields);
    }

    protected boolean isHtml() {
        if (getCurrentValue().size() < 2) {
            return false;
        }
        return getCurrentValue().get(1).equals("text/html");
    }

    protected void applyBinding() {
        if (container != null) {
            if (isHtml()) {
                if (htmlWidget == null) {
                    htmlWidget = new WebView(getRootContext());
                    container.removeAllViews();
                    container.addView(htmlWidget);
                }
                txtWidget = null;
            } else {
                if (txtWidget == null) {
                    txtWidget = new TextView(getRootContext());
                    container.removeAllViews();
                    container.addView(txtWidget);
                }
                htmlWidget = null;
            }
        }
        if (txtWidget != null) {
            txtWidget.setText(getCurrentValue().get(0));
        }
        if (htmlWidget != null) {
            htmlWidget.loadData(getCurrentValue().get(0), "text/html", "UTF-8");
        }
        if (editWidget != null) {
            editWidget.setText(getCurrentValue().get(0));
        }
    }

    @Override
    public View buildView(LayoutContext context, LayoutMode mode, Document doc,
            List<String> attributeNames, WidgetDefinition widgetDef) {

        super.buildView(context, mode, doc, attributeNames, widgetDef);

        Context ctx = context.getActivity();

        if (mode == LayoutMode.VIEW) {
            container = new LinearLayout(ctx);
            container.setOrientation(LinearLayout.VERTICAL);
            applyBinding();
            return container;
        } else {
            editWidget = new EditText(ctx);
            editWidget.setSingleLine(false);
            editWidget.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            editWidget.setLines(3);
            editWidget.setGravity(Gravity.TOP | Gravity.LEFT);
            applyBinding();
            return editWidget;
        }
    }

    @Override
    public void refreshViewFromDocument(Document doc) {
        initCurrentValueFromDocument(doc);
        applyBinding();
    }

    @Override
    public void updateModel(Document doc) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean validateBeforeModelUpdate() {
        return true;
    }

}
