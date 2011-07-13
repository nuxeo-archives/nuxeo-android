package org.nuxeo.android.automationsample;

import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;

public class AutomationClientFactory {

	public static final String TEST_SERVER = "http://android.demo.nuxeo.com/nuxeo/site/automation";
	public static final String TEST_USER = "droidUser";
	public static final String TEST_PASSWORD = "nuxeo4android";

	public static HttpAutomationClient getClient() {
		return new HttpAutomationClient(TEST_SERVER);
	}

	public static Session getSession() {
		return getClient().getSession(TEST_USER, TEST_PASSWORD);
	}

}
