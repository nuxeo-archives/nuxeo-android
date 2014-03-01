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

package org.nuxeo.android.documentprovider;

import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.PathRef;

public class LazyUpdatableDocumentsListImpl extends
        AbstractLazyUpdatebleDocumentsList implements
        LazyUpdatableDocumentsList {

    public LazyUpdatableDocumentsListImpl(Session session, String nxql,
            String[] queryParams, String sortOrder, String schemas, int pageSize) {
        super(session, nxql, queryParams, sortOrder, schemas, pageSize);
    }

    public LazyUpdatableDocumentsListImpl(OperationRequest fetchOperation,
            String pageParametrerName) {
        super(fetchOperation, pageParametrerName);
    }

    @Override
    protected OperationRequest buildUpdateOperation(Session session,
            Document updatedDocument) {
        OperationRequest updateOperation = session.newRequest(
                DocumentService.UpdateDocument).setInput(updatedDocument);
        updateOperation.set("properties",
                updatedDocument.getDirtyPropertiesAsPropertiesString());
        updateOperation.set("save", true);
        updateOperation.set("changeToken", updatedDocument.getChangeToken()); // prevent
                                                                              // dirty
                                                                              // updates
                                                                              // !
        // add dependency if needed
        // markDependencies(updateOperation, updatedDocument);
        return updateOperation;
    }

    @Override
    protected OperationRequest buildCreateOperation(Session session,
            Document newDocument) {
        PathRef parent = new PathRef(newDocument.getParentPath());
        OperationRequest createOperation = session.newRequest(
                DocumentService.CreateDocument).setInput(parent);
        createOperation.set("type", newDocument.getType());
        createOperation.set("properties",
                newDocument.getDirtyPropertiesAsPropertiesString());
        if (newDocument.getName() != null) {
            createOperation.set("name", newDocument.getName());
        }
        // add dependency if needed
        // markDependencies(createOperation, newDocument);
        return createOperation;
    }

}
