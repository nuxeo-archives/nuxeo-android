package org.nuxeo.android.pending;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.CacheKeyHelper;
import org.nuxeo.ecm.automation.client.pending.DeferredUpdatetManager;

import android.content.Context;

public class DefaultDeferedUpdateManager implements DeferredUpdatetManager {

	protected ConcurrentHashMap<String, AsyncCallback<Object>> pendingCallbacks = new ConcurrentHashMap<String, AsyncCallback<Object>>();

	protected StorageHelper storage;

	public DefaultDeferedUpdateManager(Context context) {
		storage = new StorageHelper(context);
	}

	@Override
	public void deleteDeferredUpdate(String key) {
		storage.deleteEntry(key);
	}

	@Override
	public String execDeferredUpdate(OperationRequest request,
			AsyncCallback<Object> cb, boolean exeuteNow) {

		final String requestKey = CacheKeyHelper.computeRequestKey(request);
		pendingCallbacks.put(requestKey, cb);

		request = storePendingRequest(requestKey, request);

		if (exeuteNow) {
			request.execute(new AsyncCallback<Object>() {

				@Override
				public void onError(String executionId, Throwable e) {
					AsyncCallback<Object> clientCB = pendingCallbacks.remove(requestKey);
					if (clientCB!=null) {
						clientCB.onError(requestKey, e);
					}
				}

				@Override
				public void onSuccess(String executionId, Object data) {
					AsyncCallback<Object> clientCB = pendingCallbacks.remove(requestKey);
					if (clientCB!=null) {
						clientCB.onSuccess(requestKey, data);
					}
				}
			},CacheBehavior.FORCE_REFRESH);
		}
		return requestKey;
	}

	protected OperationRequest storePendingRequest(String requestKey, OperationRequest request) {
		return storage.storeRequest(requestKey, request);
	}

	protected Map<String, OperationRequest> getPendingRequest(Session session) {
		return storage.getPendingRequests(session);
	}

	public void executePendingRequests(Session session) {

		for (Entry<String, OperationRequest> entry : getPendingRequest(session).entrySet()) {
			final String requestKey = entry.getKey();
			entry.getValue().execute(new AsyncCallback<Object>() {
				@Override
				public void onError(String executionId, Throwable e) {
					AsyncCallback<Object> clientCB = pendingCallbacks.remove(requestKey);
					if (clientCB!=null) {
						clientCB.onError(requestKey, e);
					}
				}

				@Override
				public void onSuccess(String executionId, Object data) {
					AsyncCallback<Object> clientCB = pendingCallbacks.remove(requestKey);
					if (clientCB!=null) {
						clientCB.onSuccess(requestKey, data);
					}
				}
			});
		}
	}

	public long getPendingRequestCount() {
		return storage.getEntryCount();
	}
}
