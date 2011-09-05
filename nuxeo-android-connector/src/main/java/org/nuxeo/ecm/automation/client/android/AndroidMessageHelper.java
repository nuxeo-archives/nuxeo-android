package org.nuxeo.ecm.automation.client.android;

import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;
import org.nuxeo.ecm.automation.client.broadcast.EventLifeCycle;
import org.nuxeo.ecm.automation.client.broadcast.MessageHelper;
import org.nuxeo.ecm.automation.client.cache.OperationType;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Context;
import android.content.Intent;

public class AndroidMessageHelper implements MessageHelper {

	protected final Context ctx;

	public AndroidMessageHelper(Context androidContext) {
		this.ctx=androidContext;
	}

	public void notifyDocumentOperation(Document doc, OperationType opType, EventLifeCycle state) {
		if (opType == OperationType.CREATE) {
			notifyDocumentCreated(doc, state);
		}
		else if (opType == OperationType.UPDATE) {
			notifyDocumentUpdated(doc, state);
		}
		else if (opType == OperationType.DELETE) {
			notifyDocumentDeleted(doc, state);
		}
	}

	public void notifyDocumentCreated(Document doc, EventLifeCycle state) {
		if (state== EventLifeCycle.SERVER) {
			notifyDocumentEvent(doc, NuxeoBroadcastMessages.DOCUMENT_CREATED_SERVER);
		} else if (state== EventLifeCycle.CLIENT) {
			notifyDocumentEvent(doc, NuxeoBroadcastMessages.DOCUMENT_CREATED_CLIENT);
		} else {
			notifyDocumentEvent(doc, NuxeoBroadcastMessages.DOCUMENT_CREATED_FAILED);
		}
	}

	public void notifyDocumentUpdated(Document doc, EventLifeCycle state) {
		if (state== EventLifeCycle.SERVER) {
			notifyDocumentEvent(doc, NuxeoBroadcastMessages.DOCUMENT_UPDATED_SERVER);
		} else if (state== EventLifeCycle.CLIENT) {
			notifyDocumentEvent(doc, NuxeoBroadcastMessages.DOCUMENT_UPDATED_CLIENT);
		} else {
			notifyDocumentEvent(doc, NuxeoBroadcastMessages.DOCUMENT_UPDATED_FAILED);
		}
	}

	public void notifyDocumentDeleted(Document doc, EventLifeCycle state) {
		if (state== EventLifeCycle.SERVER) {
			notifyDocumentEvent(doc, NuxeoBroadcastMessages.DOCUMENT_DELETED_SERVER);
		} else if (state== EventLifeCycle.CLIENT) {
			notifyDocumentEvent(doc, NuxeoBroadcastMessages.DOCUMENT_DELETED_CLIENT);
		} else {
			notifyDocumentEvent(doc, NuxeoBroadcastMessages.DOCUMENT_DELETED_FAILED);
		}

	}

	public void notifyDocumentEvent(Document doc, String event) {

		Intent intent = new Intent();
		intent.setAction(event);

		if (doc!=null) {
			intent.putExtra(NuxeoBroadcastMessages.EXTRA_DOCUMENT_PAYLOAD_KEY, doc);
		}
		ctx.sendBroadcast(intent);
	}

}
