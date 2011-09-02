package org.nuxeo.android.contentprovider;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.PathRef;

import android.util.Log;

public class LazyUpdatebleDocumentsListImpl extends LazyDocumentsListImpl
		implements LazyUpdatableDocumentsList {

	// store the pending created documents
	protected LinkedHashMap<String, Document> pendingCreatedDocuments = new LinkedHashMap<String, Document>();

	public LazyUpdatebleDocumentsListImpl (Session session, String nxql, String[] queryParams, String sortOrder, String schemas, int pageSize) {
		super(session, nxql, queryParams, sortOrder, schemas, pageSize);
	}

	public LazyUpdatebleDocumentsListImpl (OperationRequest fetchOperation, String pageParametrerName) {
		super(fetchOperation, pageParametrerName);
	}

	@Override
	public void updateDocument(Document updatedDocument) {
		updateDocument(updatedDocument, null);
	}

	@Override
	public void updateDocument(Document updatedDocument, OperationRequest updateOperation) {
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
			if (updateOperation==null) {
				updateOperation = buildUpdateOperation(session, updatedDocument);
			}
			session.execDeferredUpdate(updateOperation, new AsyncCallback<Object>() {

				@Override
				public void onSuccess(String executionId, Object data) {
					notifyContentChanged(page);
					// start refreshing
					refreshAll();
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

	protected OperationRequest buildUpdateOperation(Session session, Document updatedDocument) {
		OperationRequest updateOperation = session.newRequest(DocumentService.UpdateDocument).setInput(updatedDocument);
		updateOperation.set("properties", updatedDocument.getDirtyPropertiesAsPropertiesString());
		updateOperation.set("save", true);
		return updateOperation;
	}

	protected OperationRequest buildCreateOperation(Session session, Document newDocument) {
		PathRef parent = new PathRef(newDocument.getParentPath());
		OperationRequest createOperation = session.newRequest(DocumentService.CreateDocument).setInput(parent);
		createOperation.set("type", newDocument.getType());
		createOperation.set("properties", newDocument.getDirtyPropertiesAsPropertiesString());
		if (newDocument.getName()!=null) {
			createOperation.set("name", newDocument.getName());
		}
		return createOperation;
	}


	@Override
	public void createDocument(Document newDocument) {
		createDocument(newDocument, null);
	}

	@Override
	public void createDocument(Document newDocument,
			OperationRequest createOperation) {

		final String key = "NEW-" + System.currentTimeMillis();
		pendingCreatedDocuments.put(key, newDocument);

		final int notifyPage = -1;

		if (createOperation==null) {
			createOperation = buildCreateOperation(session, newDocument);
		}
		session.execDeferredUpdate(createOperation, new AsyncCallback<Object>() {

			@Override
			public void onSuccess(String executionId, Object data) {
				pendingCreatedDocuments.remove(key);
				notifyContentChanged(notifyPage);
				// start refreshing
				refreshAll();
			}

			@Override
			public void onError(String executionId, Throwable e) {
				// revert to previous
				pendingCreatedDocuments.remove(key);
				notifyContentChanged(notifyPage);
			}
		});
		// notify UI
		notifyContentChanged(notifyPage);
	}

	@Override
	protected int computeTargetPage(int position) {
		return (position - pendingCreatedDocuments.size()) / pageSize;
	}

	@Override
	protected int getRelativePositionOnPage(int globalPosition, int pageIndex) {
		if (pageIndex==0) {
			return super.getRelativePositionOnPage(globalPosition, pageIndex)-pendingCreatedDocuments.size();
		} else {
			return super.getRelativePositionOnPage(globalPosition, pageIndex);
		}
	}

	@Override
	protected int getRelativePositionOnPage() {
		int pendingCount =pendingCreatedDocuments.size();
		if (pendingCount==0) {
			return super.getRelativePositionOnPage();
		} else {
			int pos = getCurrentPosition();
			if (pos < pendingCount) {
				return -pos;
			} else {
				int targetPageIndex = computeTargetPage(pos);
				return getRelativePositionOnPage((pos - pendingCount), targetPageIndex);
			}
		}
	}

	@Override
	public Document getCurrentDocument() {
		int pendingCount =pendingCreatedDocuments.size();
		if (pendingCount==0) {
			return super.getCurrentDocument();
		}
		Document doc = null;
		int pos = getRelativePositionOnPage();
		if (pos < 0) {
			List<String> keys = new ArrayList<String>(pendingCreatedDocuments.keySet());
			return pendingCreatedDocuments.get(keys.get(-pos-1));
		}
		Documents currentDocs = getCurrentPage();
		if (currentDocs.size()> pos) {
			return currentDocs.get(pos);
		} else {
			Log.e(LazyDocumentsListImpl.class.getSimpleName(), "wrong index");
			return null;
		}
	}

	public int getCurrentSize() {
		return pendingCreatedDocuments.size() + super.getCurrentSize();
	}

}
