/*
 * (C) Copyright 2006-2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     bstefanescu
 */
package org.nuxeo.ecm.automation.client.jaxrs.util;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.automation.client.jaxrs.model.OperationDocumentation;
import org.nuxeo.ecm.automation.client.jaxrs.model.OperationDocumentation.Param;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyList;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class JSONExporter {

    public static String toJSON(List<OperationDocumentation> docs)
            throws IOException {
        StringWriter writer = new StringWriter();
        try {
            toJSON(docs, writer);
        } catch (JSONException e) {
            throw new IOException("Error during JSON marshaling "
                    + e.getMessage());
        }
        return writer.toString();
    }

    public static void toJSON(List<OperationDocumentation> docs, Writer writer)
            throws IOException, JSONException {
        JSONObject json = new JSONObject();
        JSONArray ops = new JSONArray();
        for (OperationDocumentation doc : docs) {
            JSONObject op = toJSON(doc);
            ops.put(op);
        }
        json.put("operations", ops);
        writer.write(json.toString(2));
    }

    public static JSONObject toJSON(OperationDocumentation doc)
            throws JSONException {
        JSONObject op = new JSONObject();
        op.put("id", doc.id);
        op.put("label", doc.label);
        op.put("category", doc.category);
        op.put("requires", doc.requires);
        op.put("description", doc.description);
        if (doc.since != null && doc.since.length() > 0) {
            op.put("since", doc.since);
        }
        op.put("url", doc.url);
        JSONArray sig = new JSONArray();
        for (String in : doc.signature) {
            sig.put(in);
        }
        op.put("signature", sig);
        JSONArray params = new JSONArray();
        for (Param p : doc.params) {
            JSONObject param = new JSONObject();
            param.put("name", p.name);
            param.put("type", p.type);
            param.put("required", p.isRequired);
            param.put("widget", p.widget);
            JSONArray ar = new JSONArray();
            for (String value : p.values) {
                ar.put(value);
            }
            param.put("values", ar);
            params.put(param);
        }
        op.put("params", params);
        return op;
    }

    public static OperationDocumentation fromJSON(JSONObject json)
            throws JSONException {
        OperationDocumentation op = new OperationDocumentation(
                json.getString("id"));
        op.category = json.optString("label", null);
        op.category = json.optString("category", null);
        op.requires = json.optString("requires", null);
        op.description = json.optString("description", null);
        op.url = json.optString("url", op.id);
        JSONArray sig = json.optJSONArray("signature");
        if (sig != null) {
            op.signature = new String[sig.length()];
            for (int j = 0, size = sig.length(); j < size; j++) {
                op.signature[j] = sig.getString(j);
            }
        }
        // read params
        JSONArray params = json.optJSONArray("params");
        if (params != null) {
            op.params = new ArrayList<Param>(params.length());
            for (int j = 0, size = params.length(); j < size; j++) {
                JSONObject p = params.getJSONObject(j);
                Param para = new Param();
                para.name = p.optString("name", null);
                para.type = p.optString("type", null);
                para.isRequired = p.optBoolean("required", false);
                para.widget = p.optString("widget", null);
                JSONArray ar = p.optJSONArray("values");
                if (ar != null) {
                    para.values = new String[ar.length()];
                    for (int k = 0, size2 = ar.length(); k < size2; k++) {
                        para.values[k] = ar.getString(k);
                    }
                }
                op.params.add(para);
            }
        }

        return op;
    }

    public static PropertyMap getFromJSONString(String json) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            return getFromJSONObject(jsonObject);
        } catch (JSONException e) {
            throw new RuntimeException("Error in JSON Parsing", e);
        }
    }

    public static PropertyMap getFromJSONObject(JSONObject jsonMap) {
        PropertyMap map = new PropertyMap();
        try {
            Iterator<String> keyIterator = jsonMap.keys();
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                Object value = jsonMap.get(key);
                if (value instanceof JSONObject) {
                    map.set(key, getFromJSONObject((JSONObject) value));
                } else if (value instanceof JSONArray) {
                    map.set(key, getFromJSONArray((JSONArray) value));
                } else {
                    map.map().put(key, value);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static PropertyList getFromJSONArray(JSONArray jsonArray) {
        PropertyList list = new PropertyList();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                Object value = jsonArray.get(i);
                if (value instanceof JSONObject) {
                    list.add(getFromJSONObject((JSONObject) value));
                } else if (value instanceof JSONArray) {
                    list.add(getFromJSONArray((JSONArray) value));
                } else {
                    list.list().add((Serializable) value);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String toJSON(PropertyMap props) {

        // JSONObject json = new JSONObject(props.map());
        try {
            JSONObject json = asJSONObject(props);
            return json.toString();
        } catch (Exception e) {
            throw new RuntimeException("JSON parsing error");
        }
    }

    protected static JSONObject asJSONObject(PropertyMap props)
            throws JSONException {
        JSONObject json = new JSONObject();

        for (Entry<String, Object> entry : props.map().entrySet()) {
            if (entry.getValue() instanceof PropertyMap) {
                json.put(entry.getKey(),
                        asJSONObject((PropertyMap) entry.getValue()));
            } else if (entry.getValue() instanceof PropertyList) {
                json.put(entry.getKey(),
                        asJSONArray((PropertyList) entry.getValue()));
            } else {
                json.put(entry.getKey(), entry.getValue());
            }
        }
        return json;
    }

    protected static JSONArray asJSONArray(PropertyList props)
            throws JSONException {

        JSONArray array = new JSONArray();
        for (Serializable value : props.list()) {
            if (value instanceof PropertyMap) {
                array.put(asJSONObject((PropertyMap) value));
            } else if (value instanceof PropertyList) {
                array.put(asJSONArray((PropertyList) value));
            } else {
                array.put(value);
            }
        }

        return array;
    }
}
