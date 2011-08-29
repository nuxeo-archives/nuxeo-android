package org.nuxeo.android.contentprovider;

import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

public interface LazyUpdatableDocumentsList extends LazyDocumentsList {

	void updateDocument(Document updatedDocument);

}
