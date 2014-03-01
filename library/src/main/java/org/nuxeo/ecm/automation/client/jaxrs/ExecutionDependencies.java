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

package org.nuxeo.ecm.automation.client.jaxrs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.automation.client.jaxrs.Dependency.DependencyType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExecutionDependencies implements Iterable<Dependency> {

    protected final List<Dependency> dependencies = new ArrayList<Dependency>();

    public void add(DependencyType type, String token) {
        dependencies.add(new Dependency(type, token));
    }

    @Override
    public Iterator<Dependency> iterator() {
        return dependencies.iterator();
    }

    public void markAsResolved(String key) {
        Iterator<Dependency> it = dependencies.iterator();
        while (it.hasNext()) {
            Dependency dep = it.next();
            if (dep.token.equals(key)) {
                it.remove();
            }
        }
    }

    public boolean resolved() {
        return dependencies.size() == 0;
    }

    public String asJSON() {
        JSONArray array = new JSONArray();
        for (Dependency dep : dependencies) {
            JSONObject ob = new JSONObject();
            try {
                ob.put(dep.getType().toString(), dep.getToken());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            array.put(ob);
        }
        return array.toString();
    }

    public static ExecutionDependencies fromJSON(String json) {
        ExecutionDependencies deps = new ExecutionDependencies();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject ob = array.getJSONObject(i);
                DependencyType type = DependencyType.fromString((String) ob.keys().next());
                deps.add(type, ob.getString(type.toString()));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return deps;
    }

    public int size() {
        return dependencies.size();
    }

    public void merge(ExecutionDependencies deps) {
        if (deps != null) {
            for (Dependency dep : deps) {
                dependencies.add(dep);
            }
        }
    }
}
