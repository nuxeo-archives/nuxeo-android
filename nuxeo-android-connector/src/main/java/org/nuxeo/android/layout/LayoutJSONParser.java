package org.nuxeo.android.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.android.layout.selectOptions.SelectOptions;

public class LayoutJSONParser {

	public final static String WIDGETS_DEF = "widgetDefinitions";
	public final static String ROWS_DEF = "rows";
	public final static String OPTIONS_DEF = "selectOptions";

	public static LayoutDefinition readDefinition(String jsonString) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonString);
		return readDefinition(jsonObject);
	}

	public static LayoutDefinition readDefinition(JSONObject jsonObject) throws JSONException {

		JSONArray widgets = jsonObject.getJSONArray(WIDGETS_DEF);
		JSONArray rows = jsonObject.getJSONArray(ROWS_DEF);

	    Map<String, WidgetDefinition> widgetDefs = new HashMap<String, WidgetDefinition>();

		List<LayoutRow> rowDefs = new ArrayList<LayoutRow>();

		for (int i = 0 ; i< widgets.length(); i++) {
			JSONObject w = widgets.getJSONObject(i);
			WidgetDefinition wDef = new WidgetDefinition(w.getString("name"), w.getString("type"), w.getString("label"), w.getString("attributeName"));
			widgetDefs.put(w.getString("name"), wDef);

			JSONArray options = w.optJSONArray(OPTIONS_DEF);
			if (options!=null) {
				wDef.setSelectOptions(new SelectOptions(options));
			}

		}
		for (int i = 0 ; i< rows.length(); i++) {
			JSONArray r = rows.getJSONArray(i);
			List<String> widgetNames = new ArrayList<String>();
			for (int j = 0; j < r.length(); j++) {
				widgetNames.add(r.getString(j));
			}
			rowDefs.add(new LayoutRow(widgetNames));
		}
		return new LayoutDefinition(widgetDefs, rowDefs);
	}

}
