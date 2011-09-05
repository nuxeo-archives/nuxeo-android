package org.nuxeo.ecm.automation.client.broadcast;

import org.nuxeo.ecm.automation.client.cache.OperationType;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

public interface MessageHelper {

	void notifyDocumentCreated(Document doc, EventLifeCycle state);

	void notifyDocumentUpdated(Document doc, EventLifeCycle state);

	void notifyDocumentDeleted(Document doc, EventLifeCycle state);

	void notifyDocumentOperation(Document doc, OperationType opType, EventLifeCycle state);

	void notifyDocumentEvent(Document doc, String event);

}
