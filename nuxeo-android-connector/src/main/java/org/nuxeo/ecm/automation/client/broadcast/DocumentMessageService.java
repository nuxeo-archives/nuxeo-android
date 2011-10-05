package org.nuxeo.ecm.automation.client.broadcast;

import org.nuxeo.ecm.automation.client.cache.OperationType;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.os.Bundle;

public interface DocumentMessageService {

	void notifyDocumentCreated(Document doc, EventLifeCycle state, Bundle extra);

	void notifyDocumentUpdated(Document doc, EventLifeCycle state, Bundle extra);

	void notifyDocumentDeleted(Document doc, EventLifeCycle state, Bundle extra);

	void notifyDocumentOperation(Document doc, OperationType opType, EventLifeCycle state, Bundle extra);

	void notifyDocumentEvent(Document doc, String event, Bundle extra);

}
