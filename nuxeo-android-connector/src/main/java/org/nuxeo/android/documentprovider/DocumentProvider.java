package org.nuxeo.android.documentprovider;

import java.util.List;

import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;

public interface DocumentProvider {

	void registerNamedProvider(String name, OperationRequest fetchOperation, String pageParametrerName, boolean readOnly, boolean persistent, String exposedMimeType);

	void registerNamedProvider(Session session, String name, String nxql, int pageSize, boolean readOnly, boolean persistent, String exposedMimeType);

	void registerNamedProvider(LazyDocumentsList docList, boolean persistent);

	void unregisterNamedProvider(String name);

	LazyDocumentsList getReadOnlyDocumentsList(String name, Session session);

	LazyUpdatableDocumentsList getDocumentsList(String name,  Session session);

	List<String> listProviderNames();

	boolean isRegistred(String name);

}
