package org.nuxeo.ecm.automation.client.cache;

import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;

import android.os.Handler;

public interface DeferredUpdateManager {

	String execDeferredUpdate(OperationRequest request, AsyncCallback<Object> cb, boolean exeuteNow);

	void deleteDeferredUpdate(String key);

	void executePendingRequests(Session session);

	void executePendingRequests(Session session, final Handler uiNotifier);

	long getPendingRequestCount();

	void purgePendingUpdates();
}
