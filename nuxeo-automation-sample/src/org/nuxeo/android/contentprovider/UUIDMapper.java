package org.nuxeo.android.contentprovider;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

public class UUIDMapper {

	protected ConcurrentHashMap<String, Long> UUID2IDs = new ConcurrentHashMap<String, Long>();

	protected Long lastID = new Long(0);

	public Long getIdentifier(Document doc) {
		return getIdentifier(doc.getId());
	}

	private Long generate(String UUID) {
		lastID+=1;
		return lastID;
	}

	public Long getIdentifier(String UUID) {
		if (!UUID2IDs.containsKey(UUID)) { // avoid generate non continuous IDs ...
			UUID2IDs.putIfAbsent(UUID, generate(UUID));
		}
		return UUID2IDs.get(UUID);
	}

	public String resolveIdentifier(Long id) {
		for (Entry<String, Long> entry : UUID2IDs.entrySet()) {
			if (entry.getValue().equals(id)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public void bind(Documents docs) {
		for (Document doc : docs) {
			getIdentifier(doc);
		}
	}

	public void release(Documents docs) {
		for (Document doc : docs) {
			UUID2IDs.remove(doc.getId());
		}
	}
}
