package org.nuxeo.ecm.automation.client.jaxrs.impl;

public class NotAvailableOffline extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NotAvailableOffline(String message, Throwable t) {
		super(message, t);
	}

	public NotAvailableOffline(String message) {
		super(message);
	}

}
