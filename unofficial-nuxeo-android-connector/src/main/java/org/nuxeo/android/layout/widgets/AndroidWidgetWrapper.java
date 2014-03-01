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
import java.util.Map;

import org.nuxeo.android.layout.ActivityResultHandler;
import org.nuxeo.android.layout.LayoutContext;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.WidgetDefinition;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.view.View;

public interface AndroidWidgetWrapper {

    View buildView(LayoutContext context, LayoutMode mode, Document doc,
            List<String> attributeNames, WidgetDefinition widgetDef);

    boolean validateBeforeModelUpdate();

    void updateModel(Document doc);

    void refreshViewFromDocument(Document doc);

    Map<Integer, ActivityResultHandler> getAndFlushPendingActivityResultHandler();

}
