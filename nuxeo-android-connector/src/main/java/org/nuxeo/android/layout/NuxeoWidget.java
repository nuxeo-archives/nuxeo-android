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

import java.util.Map;

import org.nuxeo.android.layout.widgets.AndroidWidgetWrapper;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.view.View;

public class NuxeoWidget {

    protected final WidgetDefinition widgetDef;

    protected final View view;

    protected final AndroidWidgetWrapper wrapper;

    public NuxeoWidget(WidgetDefinition widgetDef, View view,
            AndroidWidgetWrapper wrapper) {
        this.widgetDef = widgetDef;
        this.view = view;
        this.wrapper = wrapper;
        if (wrapper == null) {
            throw new RuntimeException(
                    "No native Widget wrapper registred for WidgetType "
                            + widgetDef.getType());
        }
    }

    public void applyChanges(Document doc) {
        if (view != null) {
            wrapper.updateModel(doc);
        } else {
            throw new RuntimeException("Can not apply changes with a null view");
        }
    }

    public void refresh(Document doc) {
        if (view != null) {
            wrapper.refreshViewFromDocument(doc);
        } else {
            throw new RuntimeException("Can not refresh a null view");
        }
    }

    public Map<Integer, ActivityResultHandler> getAndFlushPendingActivityResultHandler() {
        return wrapper.getAndFlushPendingActivityResultHandler();
    }

    public WidgetDefinition getWidgetDef() {
        return widgetDef;
    }

    public View getView() {
        return view;
    }

}
