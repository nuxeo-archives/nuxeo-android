package org.nuxeo.ecm.automation.client.android;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;
import org.nuxeo.android.cache.sql.DefferedUpdateTableWrapper;
import org.nuxeo.android.cache.sql.SQLStateManager;
import org.nuxeo.ecm.automation.client.broadcast.EventLifeCycle;
import org.nuxeo.ecm.automation.client.broadcast.MessageHelper;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.cache.CachedOperationRequest;
import org.nuxeo.ecm.automation.client.cache.DeferredUpdateManager;
import org.nuxeo.ecm.automation.client.cache.OperationType;
import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.CacheKeyHelper;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.os.Bundle;
import android.os.Handler;

public class AndroidDeferedUpdateManager implements DeferredUpdateManager {

	protected ConcurrentHashMap<String, AsyncCallback<Object>> pendingCallbacks = new ConcurrentHashMap<String, AsyncCallback<Object>>();

	protected final SQLStateManager sqlStateManager;

	public AndroidDeferedUpdateManager(SQLStateManager sqlStateManager) {
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
			AsyncCallback<Object> cb,final OperationType opType,  boolean exeuteNow) {

		final String requestKey = CacheKeyHelper.computeRequestKey(request);
		pendingCallbacks.put(requestKey, cb);

		request = storePendingRequest(requestKey, request, opType);
		final MessageHelper messageHelper =request.getSession().getMessageHelper();

		if (exeuteNow) {
			request.execute(new AsyncCallback<Object>() {

				@Override
				public void onError(String executionId, Throwable e) {
					AsyncCallback<Object> clientCB = pendingCallbacks.remove(requestKey);
					if (clientCB!=null) {
						clientCB.onError(requestKey, e);
					}
					// Send Create/Update/Delete after event
					Bundle extra = new Bundle();
					extra.putString(NuxeoBroadcastMessages.EXTRA_REQUESTID_PAYLOAD_KEY, executionId);
					messageHelper.notifyDocumentOperation(null,opType, EventLifeCycle.FAILED, extra);
				}

				@Override
				public void onSuccess(String executionId, Object data) {
					deleteDeferredUpdate(requestKey);
					AsyncCallback<Object> clientCB = pendingCallbacks.remove(requestKey);
					if (clientCB!=null) {
						clientCB.onSuccess(requestKey, data);
					}
					// Send Create/Update/Delete after event
					Document doc = null;
					if (data!=null && data instanceof Document) {
						doc = (Document) data;
					}
					Bundle extra = new Bundle();
					extra.putString(NuxeoBroadcastMessages.EXTRA_REQUESTID_PAYLOAD_KEY, executionId);
					messageHelper.notifyDocumentOperation(doc,opType, EventLifeCycle.SERVER, extra);
				}

			},CacheBehavior.FORCE_REFRESH);
		}
		return requestKey;
	}

	protected OperationRequest storePendingRequest(String requestKey, OperationRequest request, OperationType opType) {
		return getTableWrapper().storeRequest(requestKey, request, opType);
	}

	protected List<CachedOperationRequest> getPendingRequest(Session session) {
		return getTableWrapper().getPendingRequests(session);
	}

	public void executePendingRequests(Session session) {
		executePendingRequests(session, null);
	}

	public void executePendingRequests(Session session, final Handler uiNotifier) {

		final MessageHelper messageHelper =session.getMessageHelper();

		for (CachedOperationRequest op : getPendingRequest(session)) {
			final String requestKey = op.getOperationKey();
			final OperationType opType = op.getOpType();
			op.getRequest().execute(new AsyncCallback<Object>() {
				@Override
				public void onError(String executionId, Throwable e) {
					AsyncCallback<Object> clientCB = pendingCallbacks.remove(requestKey);
					if (clientCB!=null) {
						clientCB.onError(requestKey, e);
					}
					if (uiNotifier!=null) {
						uiNotifier.sendEmptyMessage(0);
					}
					// Send Create/Update/Delete after event
					Bundle extra = new Bundle();
					extra.putString(NuxeoBroadcastMessages.EXTRA_REQUESTID_PAYLOAD_KEY, executionId);
					messageHelper.notifyDocumentOperation(null,opType, EventLifeCycle.FAILED, extra);
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
					// Send Create/Update/Delete after event
					Document doc = null;
					if (data!=null && data instanceof Document) {
						doc = (Document) data;
					}
					Bundle extra = new Bundle();
					extra.putString(NuxeoBroadcastMessages.EXTRA_REQUESTID_PAYLOAD_KEY, executionId);
					messageHelper.notifyDocumentOperation(doc,opType, EventLifeCycle.SERVER, extra);
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
