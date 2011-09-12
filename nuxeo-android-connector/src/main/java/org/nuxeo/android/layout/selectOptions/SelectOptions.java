package org.nuxeo.android.layout.selectOptions;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SelectOptions {

	protected List<String> itemValues = new ArrayList<String>();
	protected List<String> itemLabels = new ArrayList<String>();

	public SelectOptions() {
	}

	public SelectOptions(JSONArray array) {
		for (int i =0; i < array.length(); i++) {
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
		if (value==null) {
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
		if (value==null) {
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
		if (idx>=0) {
			return itemLabels.get(idx);
		} else {
			return "";
		}
	}
}
