package org.nuxeo.ecm.automation.client.cache;

import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;

public class CachedOperationRequest {

	protected final OperationRequest request;

	protected final String operationKey;

	protected final OperationType opType;

	public CachedOperationRequest(OperationRequest request,String operationKey, OperationType opType ) {
		this.request = request;
		this.operationKey = operationKey;
		this.opType = opType;
	}

	public OperationRequest getRequest() {
		return request;
	}

	public String getOperationKey() {
		return operationKey;
	}

	public OperationType getOpType() {
		return opType;
	}

}
