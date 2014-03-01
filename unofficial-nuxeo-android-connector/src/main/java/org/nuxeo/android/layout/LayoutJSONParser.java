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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.android.layout.selectOptions.SelectOptions;

import android.util.Log;

public class LayoutJSONParser {

    public final static String WIDGETS_DEF = "widgets";

    public final static String ROWS_DEF = "rows";

    public final static String OPTIONS_DEF = "selectOptions";

    public static LayoutDefinition readDefinition(String jsonString)
            throws JSONException {
        jsonString = jsonString.trim();
        if (jsonString.startsWith("[")) {
            LayoutDefinition globalDefinition = null;
            JSONArray array = new JSONArray(jsonString);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                LayoutDefinition def = readDefinition(jsonObject);
                if (globalDefinition == null) {
                    globalDefinition = def;
                } else {
                    globalDefinition.merge(def);
                }
            }
            return globalDefinition;
        } else {
            JSONObject jsonObject = new JSONObject(jsonString);
            return readDefinition(jsonObject);
        }
    }

    public static LayoutDefinition readDefinition(JSONObject jsonObject)
            throws JSONException {

        JSONArray widgets = jsonObject.getJSONArray(WIDGETS_DEF);
        JSONArray rows = jsonObject.getJSONArray(ROWS_DEF);

        Map<String, WidgetDefinition> widgetDefs = new HashMap<String, WidgetDefinition>();

        List<LayoutRow> rowDefs = new ArrayList<LayoutRow>();

        for (int i = 0; i < widgets.length(); i++) {
            try {
                JSONObject w = widgets.getJSONObject(i);
                JSONArray fields = w.getJSONArray("fields");
                JSONObject labels = w.getJSONObject("labels");
                List<String> fieldNames = new ArrayList<String>();
                for (int j = 0; j < fields.length(); j++) {
                    fieldNames.add(fields.getJSONObject(j).getString(
                            "fieldName"));
                }
                JSONObject properties = w.optJSONObject("properties");
                if (properties != null && properties.has("any")) {
                    properties = properties.getJSONObject("any");
                }
                WidgetDefinition wDef = new WidgetDefinition(
                        w.getString("name"), w.getString("type"),
                        labels.getString("any"), fieldNames, properties);
                widgetDefs.put(w.getString("name"), wDef);

                JSONArray options = w.optJSONArray(OPTIONS_DEF);
                if (options != null) {
                    wDef.setSelectOptions(new SelectOptions(options));
                }
            } catch (JSONException e) {
                Log.e(LayoutJSONParser.class.getSimpleName(),
                        "Error while parling widget " + i, e);
                throw e;
            }

        }
        for (int i = 0; i < rows.length(); i++) {
            JSONObject rowObject = rows.getJSONObject(i);
            JSONArray w = rowObject.getJSONArray("widgets");
            List<String> widgetNames = new ArrayList<String>();
            for (int j = 0; j < w.length(); j++) {
                widgetNames.add(w.getJSONObject(j).getString("name"));
            }
            rowDefs.add(new LayoutRow(widgetNames));
        }
        return new LayoutDefinition(widgetDefs, rowDefs);
    }
}
