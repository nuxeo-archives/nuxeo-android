package org.nuxeo.android.layout.widgets;

import java.util.HashMap;
import java.util.Map;

public class AndroidWidgetMapper {

	protected static AndroidWidgetMapper instance = null;

	protected final static Map<String, Class <? extends AndroidWidgetWrapper>> wrappers = new HashMap<String, Class <? extends AndroidWidgetWrapper>>();

	protected AndroidWidgetMapper() {
		registerDefaultWrappers();
	}

	public static AndroidWidgetMapper getInstance() {
		if (instance==null) {
			instance = new AndroidWidgetMapper();
		}
		return instance;
	}

	public static void registerWidgetWrapper(String type,  Class <? extends AndroidWidgetWrapper> wrapperClass) {
		wrappers.put(type, wrapperClass);
	}

	protected void registerDefaultWrappers() {
		registerWidgetWrapper("text", TextWidgetWrapper.class);
		registerWidgetWrapper("date", DateWidgetWrapper.class);
		registerWidgetWrapper("selectOne", SpinnerWidgetWrapper.class);
		registerWidgetWrapper("blob", BlobWidgetWrapper.class);
	}

	public AndroidWidgetWrapper getWidgetWrapper(String type) {
		try {
			AndroidWidgetWrapper wrapper =  wrappers.get(type).newInstance();
			return wrapper;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
