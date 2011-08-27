package org.nuxeo.android.async;

public interface OperationCallback {

	void onError(String executionId, Throwable e);

    void onSuccess(String executionId,Object result);

}
