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

package org.nuxeo.android.layout.selectOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SelectOptions {

    protected List<String> itemValues = new ArrayList<String>();

    protected List<String> itemLabels = new ArrayList<String>();

    public SelectOptions() {
    }

    public SelectOptions(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject ob = array.getJSONObject(i);
                add(ob.getString("itemValue"), ob.getString("itemLabel"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void add(String itemValue, String itemLabel) {
        itemValues.add(itemValue);
        itemLabels.add(itemLabel);
    }

    public List<String> getItemValues() {
        return itemValues;
    }

    public List<String> getItemLabels() {
        return itemLabels;
    }

    public String getItemValue(int idx) {
        return itemValues.get(idx);
    }

    public String getItemLabel(int idx) {
        return itemLabels.get(idx);
    }

    public int getValueIndex(String value) {
        if (value == null) {
            return -1;
        }
        for (int i = 0; i < itemValues.size(); i++) {
            if (itemValues.get(i).equals(value)) {
                return i;
            }
        }
        return -1;
    }

    public int getLabelIndex(String value) {
        if (value == null) {
            return -1;
        }
        for (int i = 0; i < itemLabels.size(); i++) {
            if (itemLabels.get(i).equals(value)) {
                return i;
            }
        }
        return -1;
    }

    public String getLabel(String value) {
        int idx = getValueIndex(value);
        if (idx >= 0) {
            return itemLabels.get(idx);
        } else {
            return value;
        }
    }
}
