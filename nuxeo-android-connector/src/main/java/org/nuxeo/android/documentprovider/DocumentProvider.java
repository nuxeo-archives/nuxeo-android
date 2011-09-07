package org.nuxeo.android.documentprovider;

import org.nuxeo.ecm.automation.client.jaxrs.Session;

public interface DocumentProvider {

	void registerNamedProvider(LazyDocumentsList docList, boolean persistent);

	void unregisterNamedProvider(String name);

	LazyDocumentsList getReadOnlyProvider(String name, Session session);

	LazyUpdatableDocumentsList getUpdatebleProvider(String name,  Session session);

}
