/*
 * (C) Copyright 2011-2013 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
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

import org.json.JSONArray;
import org.json.JSONObject;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DocumentManager extends DocumentService {

    public DocumentManager(Session session) {
        super(session);
    }

    public Document getDocument(DocRef docRef, boolean refresh)
            throws Exception {
        OperationRequest fetchOperation = session.newRequest(
                DocumentService.FetchDocument).set("value", docRef);
        fetchOperation.setHeader("X-NXDocumentProperties", "*");
        byte cacheFlag = CacheBehavior.STORE;
        if (refresh) {
            cacheFlag = (byte) (cacheFlag | CacheBehavior.FORCE_REFRESH);
        }
        return (Document) fetchOperation.execute(cacheFlag);
    }

    public Documents query(String nxql, String[] queryParams,
            String[] sortInfo, String schemaList, int page, int pageSize,
            byte cacheFlags) throws Exception {

    	nxql = nxql.replaceAll("'", "\"");
        OperationRequest fetchOperation = session.newRequest(
                "Document.PageProvider").set("query", nxql).set("pageSize",
                pageSize).set("page", 0);
        if (queryParams != null) {
            fetchOperation.set("queryParams", queryParams);
        }
        if (sortInfo != null) {
            fetchOperation.set("sortInfo", sortInfo);
        }
        // define returned properties
        if (schemaList == null) {
            schemaList = "common,dublincore";
        }
        fetchOperation.setHeader("X-NXDocumentProperties", schemaList);

        Documents docs = (Documents) fetchOperation.execute(cacheFlags);
        return docs;

    }

    public Document getUserHome() throws Exception {
        return (Document) session.newRequest("UserWorkspace.Get").execute();
    }

    public List<JSONObject> getAuditEntriesForDocument(String docId,
            boolean refresh) throws Exception {

        byte cacheFlag = CacheBehavior.STORE;
        if (refresh) {
            cacheFlag = (byte) (cacheFlag | CacheBehavior.FORCE_REFRESH);
        }
        List<JSONObject> result = new ArrayList<JSONObject>();
        String auditQuery = "from LogEntry log" + " WHERE log.docUUID = '"
                + docId + "'" + "   AND log.docLifeCycle IS NOT NULL"
                + "   AND log.docLifeCycle <> 'undefined'"
                + " ORDER BY log.eventDate DESC";

        Blob blob = (Blob) session.newRequest("Audit.Query").set("query",
                auditQuery).set("maxResults", 5).execute(cacheFlag);
        if (blob != null) {
            String jsonData = readBlobAsString(blob);
            JSONArray array = new JSONArray(jsonData);
            for (int i = 0; i < array.length(); i++) {
                result.add(array.getJSONObject(i));
            }
        }
        return result;
    }

    public List<JSONObject> getUserTasks(boolean refresh) throws Exception {

        byte cacheFlag = CacheBehavior.STORE;
        if (refresh) {
            cacheFlag = (byte) (cacheFlag | CacheBehavior.FORCE_REFRESH);
        }

        List<JSONObject> result = new ArrayList<JSONObject>();
        Blob blob = (Blob) getSession().newRequest("Workflow.GetTask").execute(
                cacheFlag);
        if (blob != null) {
            String jsonData = readBlobAsString(blob);
            JSONArray array = new JSONArray(jsonData);
            for (int i = 0; i < array.length(); i++) {
                result.add(array.getJSONObject(i));
            }
        }
        return result;
    }

    protected String readBlobAsString(Blob blob) throws Exception {
        StringBuffer sb = new StringBuffer();
        BufferedReader blobReader = null;
        try {
            blobReader = new BufferedReader(new InputStreamReader(
                    blob.getStream()));
            String line;
            while ((line = blobReader.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            if (blobReader != null) {
                try {
                    blobReader.close();
                } catch (IOException e) {
                }
            }
        }
        return sb.toString();
    }

}
