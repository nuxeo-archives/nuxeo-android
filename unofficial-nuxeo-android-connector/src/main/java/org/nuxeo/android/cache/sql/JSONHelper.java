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

package org.nuxeo.android.cache.sql;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONHelper {

    public static Map<String, String> readMapFromJson(String data) {
        Map<String, String> result = new HashMap<String, String>();
        try {
            JSONObject jsonMap = new JSONObject(data);
            Iterator<String> keyIterator = jsonMap.keys();
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                result.put(key, jsonMap.getString(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
}
