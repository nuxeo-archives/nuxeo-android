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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.view.ViewGroup;

public class NuxeoLayout implements ActivityResultHandler {

    protected List<NuxeoWidget> widgets = new ArrayList<NuxeoWidget>();

    protected Document doc;

    protected final LayoutContext context;
    
    public NuxeoLayout(LayoutContext context, Document doc) {
        this.context = context;
        this.doc = doc;
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {

        for (NuxeoWidget widget : widgets) {
            Map<Integer, ActivityResultHandler> handlers = widget.getAndFlushPendingActivityResultHandler();
            if (handlers != null) {
                ActivityResultHandler handler = handlers.get(requestCode);
                if (handler != null) {
                    handler.onActivityResult(requestCode, resultCode, data);
                    return true;
                }
            }
        }
        return false;
    }

    public ViewGroup getContainer() {
        return context.getRootView();
    }

    void addWidgets(List<NuxeoWidget> newwidgets) {
        widgets.addAll(newwidgets);
    }

    public void applyChanges(Document doc) {
        this.doc = doc;
        for (NuxeoWidget widget : widgets) {
            widget.applyChanges(doc);
        }
    }

    public void refreshFromDocument(Document doc) {
        this.doc = doc;
        for (NuxeoWidget widget : widgets) {
            widget.refresh(doc);
        }
    }

}
