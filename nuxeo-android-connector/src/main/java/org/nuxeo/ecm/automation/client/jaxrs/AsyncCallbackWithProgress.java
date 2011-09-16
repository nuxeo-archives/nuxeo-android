package org.nuxeo.ecm.automation.client.jaxrs;

public interface AsyncCallbackWithProgress<T> extends AsyncCallback<T> {

	void onStart();

	void onProgressUpdate(int progress);

	void notifyProgressChange(int progress);

	void notifyStart();

    void onErrorUI(String executionId,Throwable e);

    void onSuccessUI(String executionId, T data);

}
