package org.nuxeo.android.cache.sql;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONHelper {

	public static  Map<String, String> readMapFromJson(String data) {
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
