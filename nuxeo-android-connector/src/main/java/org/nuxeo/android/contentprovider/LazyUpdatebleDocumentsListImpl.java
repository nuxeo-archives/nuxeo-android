package org.nuxeo.android.contentprovider;

import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
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
		Document beforeUpdateDocument = null;
		int updatedIdx = 0;
		for (Integer pageIdx : pages.keySet()) {
			if (updated) {
				break;
			}
			Documents docs = pages.get(pageIdx);
			for (int i = 0; i <docs.size(); i++) {
				if (docs.get(i).getId().equals(updatedDocument.getId())) {
					updatedIdx = i;
					beforeUpdateDocument = docs.get(i);
					docs.set(i, updatedDocument);
					updatedPage = pageIdx;
					updated=true;
					break;
				}
			}
		}

		if (updated) {
			// send update to server
			final int page = updatedPage;
			final Document originalDocument = beforeUpdateDocument;
			final int docIdx = updatedIdx;
			OperationRequest updateOperation = session.newRequest("Document.Update").setInput(updatedDocument);
			updateOperation.set("properties", updatedDocument.getDirtyPropertiesAsPropertiesString());
			updateOperation.set("save", true);
			session.execDeferredUpdate(updateOperation, new AsyncCallback<Object>() {

				@Override
				public void onSuccess(String executionId, Object data) {
					notifyContentChanged(page);
				}

				@Override
				public void onError(String executionId, Throwable e) {
					// revert to previous
					pages.get(page).set(docIdx, originalDocument);
					notifyContentChanged(page);
				}
			});
			// notify UI
			notifyContentChanged(updatedPage);
		}
	}

}
