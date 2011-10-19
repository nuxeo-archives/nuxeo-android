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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class LayoutDefinition {

	protected Map<String, WidgetDefinition> widgetDefs = new HashMap<String, WidgetDefinition>();

	protected List<LayoutRow> rows = new ArrayList<LayoutRow>();

	public static LayoutDefinition fromJSON(String jsonString) {
		try {
			return LayoutJSONParser.readDefinition(jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public LayoutDefinition(Map<String, WidgetDefinition> widgetDefs, List<LayoutRow> rows) {
		this.widgetDefs=widgetDefs;
		this.rows=rows;
	}

	public NuxeoLayout buildLayout(Activity ctx, Document doc, ViewGroup parent, LayoutMode mode) {
		ViewGroup container = createTopLayoutContainer(ctx, parent);
		LayoutContext context = new LayoutContext(ctx, container);
		NuxeoLayout layout = new NuxeoLayout(context, doc);
		try {
			for (LayoutRow row : rows) {
				layout.addWidgets(row.buildRow(context, doc, container, widgetDefs, mode));
			}
		} catch (Throwable t) {
			Log.e(this.getClass().getSimpleName(), "Error during Layout definition parsing", t);
		}
		return layout;
	}

	protected ViewGroup createTopLayoutContainer(Context ctx, ViewGroup parent) {
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		container.setLayoutParams(params);
		parent.addView(container);
		return container;
	}

	public void merge(LayoutDefinition def) {
		widgetDefs.putAll(def.widgetDefs);
		rows.addAll(def.rows);
	}
}
