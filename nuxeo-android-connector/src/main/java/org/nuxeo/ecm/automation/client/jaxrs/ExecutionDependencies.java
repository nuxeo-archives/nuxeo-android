package org.nuxeo.ecm.automation.client.jaxrs;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.management.RuntimeErrorException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.automation.client.jaxrs.Dependency.DependencyType;

public class ExecutionDependencies implements Iterable<Dependency> {

	protected final List<Dependency> dependencies = new CopyOnWriteArrayList<Dependency>();

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
				DependencyType type = DependencyType.valueOf((String)ob.keys().next());
				deps.add(type, ob.getString(type.toString()));
			}
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
		return deps;
	}
}
