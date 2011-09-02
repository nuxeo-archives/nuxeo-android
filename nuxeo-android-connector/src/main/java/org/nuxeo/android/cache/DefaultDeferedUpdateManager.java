package org.nuxeo.android.cache;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.nuxeo.android.cache.sql.DefferedUpdateTableWrapper;
import org.nuxeo.android.cache.sql.SQLStateManager;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.cache.DeferredUpdateManager;
import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.CacheKeyHelper;

import android.os.Handler;

public class DefaultDeferedUpdateManager implements DeferredUpdateManager {

	protected ConcurrentHashMap<String, AsyncCallback<Object>> pendingCallbacks = new ConcurrentHashMap<String, AsyncCallback<Object>>();

	protected final SQLStateManager sqlStateManager;

	public DefaultDeferedUpdateManager(SQLStateManager sqlStateManager) {
		this.sqlStateManager=sqlStateManager;
		sqlStateManager.registerWrapper(new DefferedUpdateTableWrapper());
	}

	protected DefferedUpdateTableWrapper getTableWrapper() {
		return (DefferedUpdateTableWrapper) sqlStateManager.getTableWrapper(DefferedUpdateTableWrapper.TBLNAME);
	}

	@Override
	public void deleteDeferredUpdate(String key) {
		getTableWrapper().deleteEntry(key);
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
					deleteDeferredUpdate(requestKey);
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
		return getTableWrapper().storeRequest(requestKey, request);
	}

	protected Map<String, OperationRequest> getPendingRequest(Session session) {
		return getTableWrapper().getPendingRequests(session);
	}

	public void executePendingRequests(Session session) {
		executePendingRequests(session, null);
	}

	public void executePendingRequests(Session session, final Handler uiNotifier) {

		for (Entry<String, OperationRequest> entry : getPendingRequest(session).entrySet()) {
			final String requestKey = entry.getKey();
			entry.getValue().execute(new AsyncCallback<Object>() {
				@Override
				public void onError(String executionId, Throwable e) {
					AsyncCallback<Object> clientCB = pendingCallbacks.remove(requestKey);
					if (clientCB!=null) {
						clientCB.onError(requestKey, e);
					}
					if (uiNotifier!=null) {
						uiNotifier.sendEmptyMessage(0);
					}
				}

				@Override
				public void onSuccess(String executionId, Object data) {
					deleteDeferredUpdate(requestKey);
					AsyncCallback<Object> clientCB = pendingCallbacks.remove(requestKey);
					if (clientCB!=null) {
						clientCB.onSuccess(requestKey, data);
					}
					if (uiNotifier!=null) {
						uiNotifier.sendEmptyMessage(0);
					}
				}
			},CacheBehavior.FORCE_REFRESH);
		}
	}

	public long getPendingRequestCount() {
		return getTableWrapper().getCount();
	}

	@Override
	public void purgePendingUpdates() {
		getTableWrapper().clearTable();
	}
}
