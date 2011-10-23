package org.nuxeo.ecm.automation.client.android;

import org.nuxeo.ecm.automation.client.jaxrs.LoginInfo;
import org.nuxeo.ecm.automation.client.jaxrs.spi.AbstractAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.spi.DefaultSession;
import org.nuxeo.ecm.automation.client.jaxrs.spi.OperationRegistry;

public class CachedSession extends DefaultSession {

	protected final OperationRegistry cachedRegistry;

	public CachedSession(AbstractAutomationClient client, OperationRegistry cachedregistry, LoginInfo login) {
		super(client, client.getConnector(), login);
		this.cachedRegistry=cachedregistry;
	}

	public OperationRegistry getOperationRegistry() {
		return cachedRegistry;
	}

}
