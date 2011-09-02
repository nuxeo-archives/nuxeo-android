package org.nuxeo.android.contentprovider;

import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

public interface LazyUpdatableDocumentsList extends LazyDocumentsList {

	void updateDocument(Document updatedDocument);

	void updateDocument(Document updatedDocument, OperationRequest updateOperation);

	void createDocument(Document newDocument);

	void createDocument(Document newDocument, OperationRequest createOperation);

}
