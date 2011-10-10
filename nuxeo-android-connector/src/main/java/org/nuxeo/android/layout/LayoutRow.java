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

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class LayoutRow {

	List<String> widgetNames = new ArrayList<String>();

	public LayoutRow(List<String> widgetNames) {
		this.widgetNames = widgetNames;
	}

	public List<NuxeoWidget> buildRow(LayoutContext context, Document doc, ViewGroup container, Map<String, WidgetDefinition> widgetDefs, LayoutMode mode) {
		ViewGroup rowLayout = createTopLayoutContainer(context.getActivity(), container);
		List<NuxeoWidget> widgets = new ArrayList<NuxeoWidget>();
		for (String name : widgetNames) {
			NuxeoWidget widget = widgetDefs.get(name).build(context, doc, rowLayout, mode);
			if (widget!=null) {
				widgets.add(widget);
			}
		}
		return widgets;
	}

	protected ViewGroup createTopLayoutContainer(Context ctx, ViewGroup parent) {
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		container.setLayoutParams(params);
		parent.addView(container);
		return container;
	}


}
