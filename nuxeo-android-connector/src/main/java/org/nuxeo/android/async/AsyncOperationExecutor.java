package org.nuxeo.android.async;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;

public class AsyncOperationExecutor {

	protected static AsyncOperationExecutor instance;
	protected static final int NB_THREADS = 3;
	protected static final int QUEUESIZE = 20;

	protected ThreadPoolExecutor tp = new ThreadPoolExecutor(NB_THREADS, NB_THREADS, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(QUEUESIZE));

	public static AsyncOperationExecutor getInstance() {
		if (instance==null) {
			instance = new AsyncOperationExecutor();
		}
		return instance;
	}


	public String execute(OperationRequest request, OperationCallback cb) {
		return null;
	}

}
