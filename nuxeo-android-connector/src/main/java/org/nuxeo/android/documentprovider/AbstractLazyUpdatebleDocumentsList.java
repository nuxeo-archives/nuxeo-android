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

import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.broadcast.EventLifeCycle;
import org.nuxeo.ecm.automation.client.cache.OperationType;
import org.nuxeo.ecm.automation.client.cache.TransientStateManager;
import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.ConflictException;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.Dependency.DependencyType;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.os.Bundle;
import android.util.Log;

public abstract class AbstractLazyUpdatebleDocumentsList extends LazyDocumentsListImpl implements LazyUpdatableDocumentsList{

    public AbstractLazyUpdatebleDocumentsList(Session session, String nxql,
            String[] queryParams, String sortOrder, String schemas, int pageSize) {
        super(session, nxql, queryParams, sortOrder, schemas, pageSize);
    }

    public AbstractLazyUpdatebleDocumentsList(OperationRequest fetchOperation,
            String pageParametrerName) {
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
        final String updatedUUID = updatedDocument.getId();
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
            getMessageHelper().notifyDocumentUpdated(updatedDocument, EventLifeCycle.CLIENT, null);
            // send update to server
            final int page = updatedPage;
            final Document originalDocument = beforeUpdateDocument;
            final int docIdx = updatedIdx;
            if (updateOperation==null) {
                updateOperation = buildUpdateOperation(session, updatedDocument);
            }
            String requestId = session.execDeferredUpdate(updateOperation, new AsyncCallback<Object>() {

                @Override
                public void onSuccess(String executionId, Object data) {
                    Log.i(AbstractLazyUpdatebleDocumentsList.class.getSimpleName(), "Deferred update successful");
                    // be sure to remove the transient state before we redisplay !
                    getClient().getTransientStateManager().flushTransientState(updatedUUID);
                    fetchPageAsync(page, true);
                    Log.i(AbstractLazyUpdatebleDocumentsList.class.getSimpleName(), "Refreshing updated page " + page);
                    // start refreshing
                    refreshAll();
                }

                @Override
                public void onError(String executionId, Throwable e) {
                    Log.i(AbstractLazyUpdatebleDocumentsList.class.getSimpleName(), "Deferred update failed " + e.getClass().getSimpleName());

                    if (e instanceof ConflictException) {
                        Log.i(AbstractLazyUpdatebleDocumentsList.class.getSimpleName(), "Marking document as conflicted : " + updatedUUID);
                        getClient().getTransientStateManager().markAsConflict(updatedUUID);
                    } else {
                        // revert to previous
                        pages.get(page).set(docIdx, originalDocument);
                    }
                    notifyContentChanged(page);
                }
            }, OperationType.UPDATE);
            // notify UI
            notifyContentChanged(updatedPage);
        }
    }

    @Override
    public void createDocument(Document newDocument) {
        createDocument(newDocument, null);
    }

    protected String addPendingCreatedDocument(Document newDoc) {
        pages.get(0).add(0, newDoc);
        notifyContentChanged(0);
        return newDoc.getId();
    }

    protected void removePendingCreatedDocument(String uuid) {
        Documents docs = pages.get(0);
        for (int idx = 0 ; idx < docs.size(); idx++) {
            if (docs.get(idx).getId().equals(uuid)) {
                docs.remove(idx);
                break;
            }
        }
        notifyContentChanged(0);
    }

    @Override
    public void createDocument(Document newDocument, OperationRequest createOperation) {

        final String key = addPendingCreatedDocument(newDocument);

        if (createOperation==null) {
            createOperation = buildCreateOperation(session, newDocument);
        }

        String requestId = session.execDeferredUpdate(createOperation, new AsyncCallback<Object>() {

            @Override
            public void onSuccess(String executionId, Object data) {
                Log.i(AbstractLazyUpdatebleDocumentsList.class.getSimpleName(), "Deferred creation executed successfully");
                removePendingCreatedDocument(key);
                // start refreshing
                refreshAll();
            }

            @Override
            public void onError(String executionId, Throwable e) {
                // revert to previous
                removePendingCreatedDocument(key);
                Log.e(LazyUpdatableDocumentsListImpl.class.getSimpleName(), "Deferred Creation failed", e);
            }
        }, OperationType.CREATE);

        Bundle extra = new Bundle();
        extra.putString(NuxeoBroadcastMessages.EXTRA_REQUESTID_PAYLOAD_KEY, requestId);
        extra.putString(NuxeoBroadcastMessages.EXTRA_SOURCEDOCUMENTSLIST_PAYLOAD_KEY, getName());
        getMessageHelper().notifyDocumentCreated(newDocument, EventLifeCycle.CLIENT, extra);

    }

    @Override
    protected int computeTargetPage(int position) {
        if (position < pages.get(0).size()) {
            return 0;
        }
        else {
            return 1 + ( (position - pages.get(0).size()) / pageSize);
        }
    }

    @Override
    protected int getRelativePositionOnPage(int globalPosition, int pageIndex) {
        if (pageIndex==0) {
            return globalPosition;
        } else {
            return globalPosition  - pages.get(0).size() - (pageIndex-1) * pageSize;
        }
    }

    @Override
    protected Documents afterPageFetch(int pageIdx, Documents docs) {
        TransientStateManager tsm = ((AndroidAutomationClient) session.getClient()).getTransientStateManager();
        docs = tsm.mergeTransientState(docs, pageIdx==0, getName());
        return docs;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    protected abstract OperationRequest buildUpdateOperation(Session session, Document updatedDocument);

    protected abstract OperationRequest buildCreateOperation(Session session, Document newDocument);

	protected void markDependencies(OperationRequest operation, Document doc) {
		for (String token : doc.getPendingUploads()) {
			operation.getDependencies().add(DependencyType.FILE_UPLOAD, token);
		}
	}

}