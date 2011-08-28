package org.nuxeo.android.contentprovider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.util.Log;

public class LazyDocumentsList {

	private static final double PREFETCH_TRIGGER = 0.5;

	protected Map<Integer, Documents> pages = new ConcurrentHashMap<Integer,Documents>();

	protected CopyOnWriteArrayList<String> loadingInProgress = new CopyOnWriteArrayList<String>();

	protected int currentPosition = 0;

	protected int currentPage = 0;

	protected int pageCount = 0;

	protected int pageSize = 20;

	protected int totalSize =0;

	protected final Session session;

	protected OperationRequest fetchOperation;

	protected final String pageParameterName;

	protected List<ChangeListener> listeners = new ArrayList<ChangeListener>();

	public LazyDocumentsList (Session session, String nxql, String[] queryParams, String sortOrder, String schemas, int pageSize) {

		this.pageSize = pageSize;
		this.currentPage = 0;
		this.session=session;
		this.pageParameterName = "page";

		fetchOperation = session.newRequest("Document.PageProvider").set(
				"query", nxql).set("pageSize",pageSize).set(pageParameterName,0);
		if (queryParams!=null) {
			fetchOperation.set("queryParams", queryParams);
		}
		// define returned properties
		fetchOperation.setHeader("X-NXDocumentProperties", schemas);
		fetchPageSync(currentPage);
	}

	public LazyDocumentsList (OperationRequest fetchOperation, String pageParametrerName) {
		this.pageParameterName = pageParametrerName;
		this.currentPage = 0;
		this.session=fetchOperation.getSession();
		this.fetchOperation = fetchOperation;
		fetchPageSync(currentPage);
	}

	public Documents getCurrentPage() {
		return pages.get(currentPage);
	}

	public Documents fetchAndChangeCurrentPage(int targetPage) {
		if (!getCurrentPage().isBatched()) {
			return null;
		}
		if (targetPage < getCurrentPage().getPageCount()) {
			currentPage = targetPage;
		} else {
			return null;
		}
		return fetchPageSync(targetPage);
	}

	protected Documents fetchPageSync(int targetPage) {
		if (pages.containsKey(targetPage)) {
			return pages.get(targetPage);
		}

		Documents docs = null;
		if (loadingInProgress.addIfAbsent(""+targetPage)) {
			docs = queryDocuments(currentPage, CacheBehavior.STORE, null);
			pages.put(targetPage, docs);
			notifyContentChanged(targetPage);
			return docs;
		} else {
			// block until loading completed !!!
			while (loadingInProgress.contains(""+targetPage)) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// NOP
				}
			}
			return fetchPageSync(targetPage);
		}
	}

	protected void fetchPageAsync(final int targetPage) {
		if (pages.containsKey(targetPage)) {
			return;
		}
		if (loadingInProgress.addIfAbsent(""+targetPage)) {
			queryDocuments(targetPage,CacheBehavior.STORE, new AsyncCallback<Object>() {
					@Override
					public void onError(String executionId, Throwable e) {
						Log.e(LazyDocumentsList.class.getSimpleName(), "Error during async page fetching", e);
						loadingInProgress.remove(""+ targetPage);
					}
					@Override
					public void onSuccess(String executionId, Object data) {
						Documents docs = (Documents) data;
						loadingInProgress.remove(""+targetPage);
						pages.put(targetPage, docs);
						pageCount = docs.getPageCount();
						totalSize = docs.getTotalSize();
						notifyContentChanged(targetPage);
					}
			});
		}
	}

	protected void notifyContentChanged(int page) {
		for (ChangeListener listener : listeners) {
			listener.notifyContentChanged(page);
		}
	}

	protected Documents queryDocuments(int page, byte cacheFlags, AsyncCallback<Object> cb) {
		Documents docs;
		try {
			fetchOperation.set(pageParameterName, page);
			if (cb==null) {
				docs = (Documents) fetchOperation.execute(cacheFlags);
				pageSize = docs.getPageSize();
				pageCount = docs.getPageCount();
				loadingInProgress.remove(""+ page);
				totalSize = docs.getTotalSize();
				return docs;
			} else {
				fetchOperation.execute(cb, cacheFlags);
				return null;
			}
		} catch (Exception e) {
			Log.e(LazyDocumentsList.class.getSimpleName(), "Error while fetching documents",e);
			return null;
		}
	}

	public int getCurrentPosition() {
		return currentPosition;
	}

	public int setCurrentPosition(int position) {

		int targetPageIndex = position / pageSize;
		if (targetPageIndex>=pageCount) {
			return currentPosition;
		}
		fetchAndChangeCurrentPage(targetPageIndex);
		currentPage = targetPageIndex;
		int relativePos = getRelativePositionOnPage(position, targetPageIndex);
		prefetchIfNeeded(relativePos);
		currentPosition = position;
		return currentPosition;
	}


	protected int getRelativePositionOnPage(int globalPosition, int pageIndex) {
		return globalPosition - currentPage * pageSize;
	}

	protected int getRelativePositionOnPage() {
		int pos = getCurrentPosition();
		int targetPageIndex = pos / pageSize;
		return getRelativePositionOnPage(pos, targetPageIndex);
	}

	protected void prefetchIfNeeded(int pos) {
		if (pos > PREFETCH_TRIGGER * pageSize) {
			final int pageToFetch = currentPage +1;

			if (!pages.containsKey(pageToFetch)) {
				fetchPageAsync(pageToFetch);
			}
		}
	}

	public Document getCurrentDocument() {
		int pos = getRelativePositionOnPage();
		Documents currentDocs = getCurrentPage();
		if (currentDocs.size()> pos) {
			return currentDocs.get(pos);
		} else {
			Log.e(LazyDocumentsList.class.getSimpleName(), "wrong index");
			return null;
		}
	}

	public int getPageCount() {
		return pageCount;
	}

	public int getLoadedPageCount() {
		return pages.size();
	}

	public Integer getLoadingPagesCount() {
		return loadingInProgress.size();
	}

	public Iterator<Document> getIterator() {
		return new Iterator<Document>() {

			@Override
			public boolean hasNext() {
				return currentPosition < totalSize;
			}

			@Override
			public Document next() {
				setCurrentPosition(currentPosition+1);
				return getCurrentDocument();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Can not remove Document from the iterator");
			}
		};
	}

	public Collection<Documents> getLoadedPages() {
		return pages.values();
	}

	public int getCurrentSize() {
		if (getCurrentPage()==null) {
			return 0;
		}
		if (getCurrentPage().isBatched()) {
			int actualSize = 0;
			for (Documents docs : pages.values()) {
				actualSize += docs.size();
			}
			return actualSize;
		} else {
			return getCurrentPage().size();
		}
	}

	public interface ChangeListener {
		void notifyContentChanged(int page);
	}

	public void registerListener(ChangeListener listener) {
		listeners.add(listener);
	}

	public void unregisterListener(ChangeListener listener) {
		listeners.remove(listener);
	}

}
