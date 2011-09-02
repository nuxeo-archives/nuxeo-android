package org.nuxeo.android.contentprovider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NuxeoDynamicProvidersRegistry {

	protected final static Map<String, LazyDocumentsList> documentsLists = new ConcurrentHashMap<String, LazyDocumentsList>();

	public static void registerNamedProvider(String name, LazyDocumentsList docList) {
		documentsLists.put(name, docList);
	}

	public static void unregisterNamedProvider(String name) {
		documentsLists.remove(name);
	}

	public static LazyDocumentsList getNamedProvider(String name) {
		return documentsLists.get(name);
	}

}
