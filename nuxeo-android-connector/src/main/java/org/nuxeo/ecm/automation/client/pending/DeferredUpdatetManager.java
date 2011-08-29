package org.nuxeo.ecm.automation.client.pending;

import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;

public interface DeferredUpdatetManager {

	String execDeferredUpdate(OperationRequest request, AsyncCallback<Object> cb, boolean exeuteNow);

	void deleteDeferredUpdate(String key);

	void executePendingRequests(Session session);

	long getPendingRequestCount();
}
