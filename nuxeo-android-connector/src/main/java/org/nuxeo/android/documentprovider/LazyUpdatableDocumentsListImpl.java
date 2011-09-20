package org.nuxeo.android.documentprovider;

import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.Dependency.DependencyType;
import org.nuxeo.ecm.automation.client.jaxrs.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.PathRef;


public class LazyUpdatableDocumentsListImpl extends AbstractLazyUpdatebleDocumentsList
		implements LazyUpdatableDocumentsList {

	public LazyUpdatableDocumentsListImpl (Session session, String nxql, String[] queryParams, String sortOrder, String schemas, int pageSize) {
		super(session, nxql, queryParams, sortOrder, schemas, pageSize);
	}

	public LazyUpdatableDocumentsListImpl (OperationRequest fetchOperation, String pageParametrerName) {
		super(fetchOperation, pageParametrerName);
	}

	protected OperationRequest buildUpdateOperation(Session session, Document updatedDocument) {
		OperationRequest updateOperation = session.newRequest(DocumentService.UpdateDocument).setInput(updatedDocument);
		updateOperation.set("properties", updatedDocument.getDirtyPropertiesAsPropertiesString());
		updateOperation.set("save", true);
		// add dependency if needed
		markDependencies(updateOperation, updatedDocument);
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
		// add dependency if needed
		markDependencies(createOperation, newDocument);
		return createOperation;
	}

	protected void markDependencies(OperationRequest operation, Document doc) {
		for (String token : doc.getPendingUploads()) {
			operation.getDependencies().add(DependencyType.FILE_UPLOAD, token);
		}
	}

}
