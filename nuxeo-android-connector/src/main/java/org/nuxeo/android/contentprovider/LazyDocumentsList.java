package org.nuxeo.android.contentprovider;

import java.util.Collection;
import java.util.Iterator;

import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

public interface LazyDocumentsList {

	public abstract Documents getCurrentPage();

	public abstract Documents fetchAndChangeCurrentPage(int targetPage);

	public abstract int getCurrentPosition();

	public abstract int setCurrentPosition(int position);

	public abstract Document getCurrentDocument();

	public abstract int getPageCount();

	public abstract int getLoadedPageCount();

	public abstract Integer getLoadingPagesCount();

	public abstract Iterator<Document> getIterator();

	public abstract Collection<Documents> getLoadedPages();

	public abstract int getCurrentSize();

	public abstract void registerListener(DocumentsListChangeListener listener);

	public abstract void unregisterListener(DocumentsListChangeListener listener);

	public abstract Document getDocument(int index);
}