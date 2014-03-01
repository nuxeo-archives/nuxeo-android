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

package org.nuxeo.ecm.automation.client.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;
import org.nuxeo.android.cache.sql.SQLStateManager;
import org.nuxeo.android.cache.sql.TransientStateTableWrapper;
import org.nuxeo.ecm.automation.client.cache.DocumentDeltaSet;
import org.nuxeo.ecm.automation.client.cache.OperationType;
import org.nuxeo.ecm.automation.client.cache.TransientStateManager;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import java.util.List;

public class AndroidTransientStateManager extends BroadcastReceiver implements
        TransientStateManager {

    protected final SQLStateManager stateManager;

    public AndroidTransientStateManager(Context androidContext,
            SQLStateManager stateManager) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(NuxeoBroadcastMessages.DOCUMENT_CREATED_CLIENT);
        filter.addAction(NuxeoBroadcastMessages.DOCUMENT_CREATED_SERVER);
        filter.addAction(NuxeoBroadcastMessages.DOCUMENT_UPDATED_CLIENT);
        filter.addAction(NuxeoBroadcastMessages.DOCUMENT_UPDATED_SERVER);
        filter.addAction(NuxeoBroadcastMessages.DOCUMENT_DELETED_CLIENT);
        filter.addAction(NuxeoBroadcastMessages.DOCUMENT_DELETED_SERVER);
        androidContext.registerReceiver(this, filter);
        stateManager.registerWrapper(new TransientStateTableWrapper());
        this.stateManager = stateManager;
    }

    protected TransientStateTableWrapper getTableWrapper() {
        return (TransientStateTableWrapper) stateManager.getTableWrapper(TransientStateTableWrapper.TBLNAME);
    }

    public void storeDocumentState(Document doc, OperationType opType,
            String requestId, String listName) {
        DocumentDeltaSet delta = new DocumentDeltaSet(opType, doc, requestId,
                listName);
        getTableWrapper().storeDeltaSet(delta);
    }

    @Override
    public void storeDocumentState(Document doc, OperationType opType) {
        storeDocumentState(doc, opType, null, null);
    }

    @Override
    public List<DocumentDeltaSet> getDeltaSets(List<String> ids,
            String targetListName) {
        List<DocumentDeltaSet> deltas = getTableWrapper().getDeltaSets(ids,
                targetListName);
        // XXX get Blobs
        return deltas;
    }

    @Override
    public Documents mergeTransientState(Documents docs, boolean add,
            String listName) {

        List<DocumentDeltaSet> deltas = getDeltaSets(docs.getIds(), listName);

        for (DocumentDeltaSet delta : deltas) {
            if (add && delta.getOperationType() == OperationType.CREATE
                    && !docs.containsDocWithId(delta.getId())) {
                if (listName == null || listName.equals(delta.getListName())) {
                    docs.add(0, delta.apply(null));
                }
            } else if (delta.getOperationType() == OperationType.UPDATE) {
                Document doc2Update = docs.getById(delta.getId());
                delta.apply(doc2Update);
                doc2Update.setInConflict(delta.isConflict());
            } else if (delta.getOperationType() == OperationType.DELETE) {
                docs.removeById(delta.getId());
            }
        }
        return docs;
    }

    @Override
    public void flushTransientState(String uid) {
        getTableWrapper().deleteEntry(uid);
    }

    @Override
    public void flushTransientState() {
        getTableWrapper().clearTable();
    }

    @Override
    public void onReceive(Context androidContext, Intent intent) {

        String eventName = intent.getAction();
        Document doc = (Document) intent.getExtras().get(
                NuxeoBroadcastMessages.EXTRA_DOCUMENT_PAYLOAD_KEY);
        String requestId = intent.getExtras().getString(
                NuxeoBroadcastMessages.EXTRA_REQUESTID_PAYLOAD_KEY);
        String listName = intent.getExtras().getString(
                NuxeoBroadcastMessages.EXTRA_SOURCEDOCUMENTSLIST_PAYLOAD_KEY);
        if (eventName.equals(NuxeoBroadcastMessages.DOCUMENT_CREATED_CLIENT)) {
            storeDocumentState(doc, OperationType.CREATE, requestId, listName);
        } else if (eventName.equals(NuxeoBroadcastMessages.DOCUMENT_UPDATED_CLIENT)) {
            storeDocumentState(doc, OperationType.UPDATE, requestId, listName);
        } else if (eventName.equals(NuxeoBroadcastMessages.DOCUMENT_DELETED_CLIENT)) {
            storeDocumentState(doc, OperationType.DELETE, requestId, listName);
        } else if (eventName.equals(NuxeoBroadcastMessages.DOCUMENT_UPDATED_SERVER)
                || eventName.equals(NuxeoBroadcastMessages.DOCUMENT_DELETED_SERVER)) {
            if (doc != null) {
                flushTransientState(doc.getId());
            }
            // XXX Trigger list refresh ?
        } else if (eventName.equals(NuxeoBroadcastMessages.DOCUMENT_CREATED_SERVER)) {
            if (requestId != null) {
                getTableWrapper().deleteEntryByRequestId(requestId);
            }
            // XXX Trigger list refresh ?
        }

    }

    @Override
    public void markAsConflict(String uid) {
        getTableWrapper().updateConflictMarker(uid, true);

    }

    @Override
    public void markAsResolved(String uid) {
        getTableWrapper().updateConflictMarker(uid, false);
    }

    @Override
    public long getEntryCount() {
        return getTableWrapper().getCount();
    }
}
