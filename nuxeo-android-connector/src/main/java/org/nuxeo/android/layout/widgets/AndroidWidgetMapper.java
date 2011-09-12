package org.nuxeo.android.layout.widgets;

import java.util.HashMap;
import java.util.Map;

public class AndroidWidgetMapper {

	protected static AndroidWidgetMapper instance = null;

	protected static final Map<String, AndroidWidgetWrapper> wrappers = new HashMap<String, AndroidWidgetWrapper>();

	protected AndroidWidgetMapper() {
		registerDefaultWrappers();
	}

	public static AndroidWidgetMapper getInstance() {
		if (instance==null) {
			instance = new AndroidWidgetMapper();
		}
		return instance;
	}

	public static void registerWidgetWrapper(String type, AndroidWidgetWrapper wrapper) {
		wrappers.put(type, wrapper);
	}

	protected void registerDefaultWrappers() {
		registerWidgetWrapper("text", new TextWidgetWrapper());
		registerWidgetWrapper("date", new DateWidgetWrapper());
		registerWidgetWrapper("selectOne", new SpinnerWidgetWrapper());
	}

	public AndroidWidgetWrapper getWidgetWrapper(String type) {
		return wrappers.get(type);
	}

}
