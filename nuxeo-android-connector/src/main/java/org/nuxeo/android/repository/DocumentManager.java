package org.nuxeo.android.repository;

import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.IdRef;

public class DocumentManager {

	protected final Session session;

	public DocumentManager(Session session) {
		this.session = session;
	}

	public Document getDocument(DocRef docRef) {
		throw new UnsupportedOperationException();
	}

	public Documents getChildren(Document parent) {
		return getChildren(new IdRef(parent.getId()));
	}

	public Documents getChildren(DocRef parent) {
		throw new UnsupportedOperationException();
	}

	public Document createDocument(Document newDoc) {
		throw new UnsupportedOperationException();
	}

	public Document saveDocument(Document doc) {
		throw new UnsupportedOperationException();
	}

	public Document attacheFile(Document doc, Blob blob) {
		throw new UnsupportedOperationException();
	}

	public Documents query(String nxql, String[] queryParams, String[] sortInfo, String schemaList, int page, int pageSize, byte cacheFlags) throws Exception {

		OperationRequest fetchOperation = session.newRequest("Document.PageProvider").set(
				"query", nxql).set("pageSize",pageSize).set("page",0);
		if (queryParams!=null) {
			fetchOperation.set("queryParams", queryParams);
		}
		if (sortInfo!=null) {
			fetchOperation.set("sortInfo", sortInfo);
		}
		// define returned properties
		if (schemaList==null) {
			schemaList = "common,dublincore";
		}
		fetchOperation.setHeader("X-NXDocumentProperties", schemaList);

		Documents docs = (Documents) fetchOperation.execute(cacheFlags);
		return docs;

	}



}
