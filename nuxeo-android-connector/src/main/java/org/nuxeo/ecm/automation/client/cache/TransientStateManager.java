package org.nuxeo.ecm.automation.client.cache;

import java.util.List;

import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

public interface TransientStateManager {

	void storeDocumentState(Document doc, OperationType opType);

	List<DocumentDeltaSet> getDeltaSets(List<String> ids);

	Documents mergeTransientState(Documents docs, boolean add, String listName);

	void flushTransientState(String uid);

	void flushTransientState();

}
