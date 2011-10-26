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

import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;
import org.nuxeo.ecm.automation.client.broadcast.EventLifeCycle;
import org.nuxeo.ecm.automation.client.broadcast.DocumentMessageService;
import org.nuxeo.ecm.automation.client.cache.OperationType;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AndroidMessageHelper implements DocumentMessageService {

    protected final Context ctx;

    public AndroidMessageHelper(Context androidContext) {
        this.ctx = androidContext;
    }

    public void notifyDocumentOperation(Document doc, OperationType opType,
            EventLifeCycle state, Bundle extra) {
        if (opType == OperationType.CREATE) {
            notifyDocumentCreated(doc, state, extra);
        } else if (opType == OperationType.UPDATE) {
            notifyDocumentUpdated(doc, state, extra);
        } else if (opType == OperationType.DELETE) {
            notifyDocumentDeleted(doc, state, extra);
        }
    }

    public void notifyDocumentCreated(Document doc, EventLifeCycle state,
            Bundle extra) {
        if (state == EventLifeCycle.SERVER) {
            notifyDocumentEvent(doc,
                    NuxeoBroadcastMessages.DOCUMENT_CREATED_SERVER, extra);
        } else if (state == EventLifeCycle.CLIENT) {
            notifyDocumentEvent(doc,
                    NuxeoBroadcastMessages.DOCUMENT_CREATED_CLIENT, extra);
        } else {
            notifyDocumentEvent(doc,
                    NuxeoBroadcastMessages.DOCUMENT_CREATED_FAILED, extra);
        }
    }

    public void notifyDocumentUpdated(Document doc, EventLifeCycle state,
            Bundle extra) {
        if (state == EventLifeCycle.SERVER) {
            notifyDocumentEvent(doc,
                    NuxeoBroadcastMessages.DOCUMENT_UPDATED_SERVER, extra);
        } else if (state == EventLifeCycle.CLIENT) {
            notifyDocumentEvent(doc,
                    NuxeoBroadcastMessages.DOCUMENT_UPDATED_CLIENT, extra);
        } else {
            notifyDocumentEvent(doc,
                    NuxeoBroadcastMessages.DOCUMENT_UPDATED_FAILED, extra);
        }
    }

    public void notifyDocumentDeleted(Document doc, EventLifeCycle state,
            Bundle extra) {
        if (state == EventLifeCycle.SERVER) {
            notifyDocumentEvent(doc,
                    NuxeoBroadcastMessages.DOCUMENT_DELETED_SERVER, extra);
        } else if (state == EventLifeCycle.CLIENT) {
            notifyDocumentEvent(doc,
                    NuxeoBroadcastMessages.DOCUMENT_DELETED_CLIENT, extra);
        } else {
            notifyDocumentEvent(doc,
                    NuxeoBroadcastMessages.DOCUMENT_DELETED_FAILED, extra);
        }

    }

    public void notifyDocumentEvent(Document doc, String event, Bundle extra) {

        Intent intent = new Intent();
        intent.setAction(event);

        if (doc != null) {
            intent.putExtra(NuxeoBroadcastMessages.EXTRA_DOCUMENT_PAYLOAD_KEY,
                    doc);
            Log.i(this.getClass().getSimpleName(), "Sending message " + event
                    + " on document " + doc.getId());
        } else {
            Log.i(this.getClass().getSimpleName(), "Sending message " + event);
        }
        if (extra != null) {
            intent.putExtras(extra);
        }
        ctx.sendBroadcast(intent);
    }

}
