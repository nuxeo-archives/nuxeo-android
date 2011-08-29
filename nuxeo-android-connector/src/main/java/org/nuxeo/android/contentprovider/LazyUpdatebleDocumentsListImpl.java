package org.nuxeo.android.contentprovider;

import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

public class LazyUpdatebleDocumentsListImpl extends LazyDocumentsListImpl
		implements LazyUpdatableDocumentsList {

	public LazyUpdatebleDocumentsListImpl (Session session, String nxql, String[] queryParams, String sortOrder, String schemas, int pageSize) {
		super(session, nxql, queryParams, sortOrder, schemas, pageSize);
	}

	public LazyUpdatebleDocumentsListImpl (OperationRequest fetchOperation, String pageParametrerName) {
		super(fetchOperation, pageParametrerName);
	}

	@Override
	public void updateDocument(Document updatedDocument) {
		boolean updated = false;
		int updatedPage = 0;
		for (Integer pageIdx : pages.keySet()) {
			if (updated) {
				break;
			}
			Documents docs = pages.get(pageIdx);
			for (int i = 0; i <docs.size(); i++) {
				if (docs.get(i).getId().equals(updatedDocument.getId())) {
					docs.set(i, updatedDocument);
					updatedPage = pageIdx;
					updated=true;
					break;
				}
			}
		}

		if (updated) {
			notifyContentChanged(updatedPage);
		}
	}

}
