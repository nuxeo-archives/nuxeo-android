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
package org.nuxeo.ecm.automation.client.jaxrs.spi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.automation.client.jaxrs.Constants;
import org.nuxeo.ecm.automation.client.jaxrs.LoginInfo;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.RemoteException;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.OperationDocumentation;
import org.nuxeo.ecm.automation.client.jaxrs.model.OperationInput;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyList;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;
import org.nuxeo.ecm.automation.client.jaxrs.util.JSONExporter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class JsonMarshalling {

    @SuppressWarnings("unchecked")
    public static OperationRegistry readRegistry(String content)
            throws JSONException {
        JSONObject json = new JSONObject(content);
        HashMap<String, OperationDocumentation> ops = new HashMap<String, OperationDocumentation>();
        HashMap<String, OperationDocumentation> chains = new HashMap<String, OperationDocumentation>();
        HashMap<String, String> paths = new HashMap<String, String>();
        JSONArray ar = json.getJSONArray("operations");
        if (ar != null) {
            for (int i = 0, len = ar.length(); i < len; i++) {
                JSONObject obj = ar.getJSONObject(i);
                OperationDocumentation op = JSONExporter.fromJSON(obj);
                ops.put(op.id, op);
            }
        }
        ar = json.getJSONArray("chains");
        if (ar != null) {
            for (int i = 0, len = ar.length(); i < len; i++) {
                JSONObject obj = ar.getJSONObject(i);
                OperationDocumentation op = JSONExporter.fromJSON(obj);
                chains.put(op.id, op);
            }
        }
        JSONObject pathsObj = json.getJSONObject("paths");
        if (pathsObj != null) {
            Iterator<String> it = pathsObj.keys();
            while (it.hasNext()) {
                String key = it.next();
                String value = pathsObj.getString(key);
                paths.put(key, value);
            }
        }
        return new OperationRegistry(paths, ops, chains);
    }

    public static Object readEntity(String content) throws JSONException {
        if (content.length() == 0) { // void response
            return null;
        }
        JSONObject json = new JSONObject(content);
        String type = json.getString(Constants.KEY_ENTITY_TYPE);
        if ("document".equals(type)) {
            return readDocument(json);
        } else if ("documents".equals(type)) {
            boolean batched = json.optBoolean("isPaginable", false);
            JSONArray ar = json.getJSONArray("entries");
            int size = ar.length();
            Documents docs = null;
            if (batched) {
                docs = new Documents(size, json.optInt("totalSize", 0),
                        json.optInt("pageSize", 0),
                        json.optInt("pageIndex", 0),
                        json.optInt("pageCount", 0));
            } else {
                docs = new Documents(size);
            }
            for (int i = 0; i < size; i++) {
                JSONObject obj = ar.getJSONObject(i);
                docs.add(readDocument(obj));
            }
            return docs;
        } else if ("login".equals(type)) {
            return readLogin(json);
        } else if ("exception".equals(type)) {
            throw readException(content);
        }
        throw new IllegalArgumentException("Unknown entity type: " + type);
    }

    public static RemoteException readException(String content)
            throws JSONException {
        return readException(new JSONObject(content));
    }

    protected static RemoteException readException(JSONObject json)
            throws NumberFormatException, JSONException {
        return new RemoteException(Integer.parseInt(json.getString("status")),
                json.optString("type", null), json.optString("message"),
                json.optString("stack", null));
    }

    protected static LoginInfo readLogin(JSONObject json) throws JSONException {
        String username = json.getString("username");
        String isAdmin = json.optString("isAdministrator", "false");
        JSONArray groups = json.optJSONArray("groups");
        HashSet<String> set = new HashSet<String>();
        if (groups != null) {
            for (int i = 0, size = groups.length(); i < size; i++) {
                set.add(groups.getString(i));
            }
        }
        return new LoginInfo(username, set, Boolean.parseBoolean(isAdmin));
    }

    protected static Document readDocument(JSONObject json)
            throws JSONException {
        String uid = json.getString("uid");
        String path = json.getString("path");
        String repoName = json.getString("repository");
        String type = json.getString("type");
        String state = json.optString("state", null);
        String lock = json.optString("lock", null);
        String title = json.optString("title", null);
        String lastModified = json.optString("lastModified", null);
        String changeToken = json.optString("changeToken", null);
        JSONArray jsonFacets = json.optJSONArray("facets");
        PropertyList facets = null;
        if (jsonFacets != null) {
            facets = (PropertyList) readValue(jsonFacets);
        }
        JSONObject jsonProps = json.optJSONObject("properties");
        PropertyMap props;
        if (jsonProps != null) {
            props = (PropertyMap) readValue(jsonProps);
        } else {
            props = new PropertyMap();
        }
        props.set("dc:title", title);
        props.set("dc:modified", lastModified);
        return new Document(repoName, uid, type, facets, changeToken, path,
                state, lock, props);
    }

    @SuppressWarnings("unchecked")
    protected static Serializable readValue(Object o) throws JSONException {
        if (o == null) {
            return null;
        }
        if (o instanceof JSONArray) {
            JSONArray ar = (JSONArray) o;
            PropertyList plist = new PropertyList();
            List<Serializable> list = plist.list();
            for (int i = 0, size = ar.length(); i < size; i++) {
                Serializable v = readValue(ar.get(i));
                if (v != null) {
                    list.add(v);
                }
            }
            return plist;
        } else if (o instanceof JSONObject) {
            JSONObject ob = (JSONObject) o;
            PropertyMap pmap = new PropertyMap();
            Map<String, Object> map = pmap.map();
            Iterator<String> keys = ob.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object v = readValue(ob.get(key));
                map.put(key, v);
            }
            return pmap;
        } else {
            return o.toString();
        }
    }

    public static String writeRequest(OperationRequest req) throws Exception {
        JSONObject entity = new JSONObject();
        OperationInput input = req.getInput();

        if (input != null && !input.isBinary()) {
            String ref = input.getInputRef();
            if (ref != null) {
                entity.put("input", ref);
            }
        }

        entity.put("params", new JSONObject(req.getParameters()));
        entity.put("context", new JSONObject(req.getContextParameters()));
        return entity.toString();
    }

}
