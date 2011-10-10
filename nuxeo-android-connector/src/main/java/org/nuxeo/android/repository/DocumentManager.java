/*
 * (C) Copyright 2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 */

package org.nuxeo.android.repository;

import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

public class DocumentManager extends DocumentService {

	public DocumentManager(Session session) {
		super(session);
	}

	public Document getDocument(DocRef docRef, boolean refresh) throws Exception {
		OperationRequest fetchOperation = session.newRequest(DocumentService.FetchDocument).set("value", docRef);
		fetchOperation.setHeader("X-NXDocumentProperties", "*");
		return (Document) fetchOperation.execute((byte) (CacheBehavior.STORE | CacheBehavior.FORCE_REFRESH));
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

	public Document getUserHome() throws Exception {
		return (Document) session.newRequest("Userworkspace.Get").execute();
	}

}
