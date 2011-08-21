package org.nuxeo.android.contentprovider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyList;

import android.database.AbstractCursor;
import android.os.Bundle;
import android.util.Log;

public class NuxeoDocumentCursor extends AbstractCursor {

	protected Map<Integer, Documents> pages = new ConcurrentHashMap<Integer,Documents>();

	protected ConcurrentHashMap<Integer, Thread> fetchInProgress = new ConcurrentHashMap<Integer,Thread>();

	protected String[] columns;

	protected int currentPage = 0;

	protected int pageSize = 20;

	protected final UUIDMapper mapper;

	protected final Session session;

	protected OperationRequest fetchOperation;

	protected final String pageParameterName;

	public NuxeoDocumentCursor (Session session, String nxql, String[] queryParams, String sortOrder, String schemas, int pageSize, UUIDMapper mapper) {

		this.pageSize = pageSize;
		this.currentPage = 0;
		if (mapper!=null) {
			this.mapper = mapper;
		} else {
			this.mapper = new UUIDMapper();
		}

		this.session=session;
		this.pageParameterName = "page";

		fetchOperation = session.newRequest("Document.PageProvider").set(
				"query", nxql).set("pageSize",pageSize).set(pageParameterName,0);
		if (queryParams!=null) {
			fetchOperation.set("queryParams", queryParams);
		}
		// define returned properties
		fetchOperation.setHeader("X-NXDocumentProperties", schemas);

		pages.put(currentPage, queryDocuments(currentPage, CacheBehavior.STORE));
	}

	public NuxeoDocumentCursor (OperationRequest fetchOperation, String pageParametrerName) {

		this.pageParameterName = pageParametrerName;
		this.currentPage = 0;
     	this.mapper = new UUIDMapper();
		this.session=fetchOperation.getSession();
		this.fetchOperation = fetchOperation;
		pages.put(currentPage, queryDocuments(currentPage, CacheBehavior.STORE));
	}

	protected Documents getCurrentPage() {
		return pages.get(currentPage);
	}

	protected boolean fetchAndChangeCurrentPage(int targetPage) {
		if (!getCurrentPage().isBatched()) {
			return false;
		}
		if (targetPage < getCurrentPage().getPageCount()) {
			currentPage = targetPage;
		} else {
			return false;
		}
		fetchPage(targetPage);
		return true;
	}

	protected void fetchPage(int targetPage) {
		pages.put(targetPage, queryDocuments(targetPage, CacheBehavior.STORE));
		onChange(true);
	}

	protected Documents queryDocuments(int page, byte cacheFlags) {
		Documents docs;
		try {
			fetchOperation.set(pageParameterName, page);
			docs = (Documents) fetchOperation.execute(cacheFlags);
			pageSize = docs.getPageSize();
		} catch (Exception e) {
			return null;
		}
		return docs;
	}

	protected int getRelativePosition() {
		int pos = getPosition();

		int targetPageIndex = pos / pageSize;
		if (!pages.containsKey(targetPageIndex)) {
			fetchAndChangeCurrentPage(targetPageIndex);
		}

		currentPage = targetPageIndex;
		pos = pos - currentPage * pageSize;

		prefetchIfNeeded(pos);

		return pos;
	}

	protected void prefetchIfNeeded(int pos) {
		if (pos > 0.7 * pageSize) {
			final int pageToFetch = currentPage +1;

			if (!pages.containsKey(pageToFetch) && !fetchInProgress.containsKey(pageToFetch)) {
				Runnable fetcher = new Runnable() {
					@Override
					public void run() {
						fetchPage(pageToFetch);
						fetchInProgress.remove(pageToFetch);
					}
				};
				Thread fetcherThread = new Thread(fetcher);
				fetchInProgress.putIfAbsent(pageToFetch, fetcherThread);
				fetcherThread.start();
			}
		}
	}

	protected Document getCurrentDocument() {
		int pos = getRelativePosition();
		Documents currentDocs = getCurrentPage();
		if (currentDocs.size()> pos) {
			return currentDocs.get(pos);
		} else {
			Log.e("NuxeoDocumentCursor", "wrong index");
			return null;
		}
	}

	protected Long getCurrentIdentifier() {
		return mapper.getIdentifier(getCurrentDocument());
	}

	@Override
	public String[] getColumnNames() {
		if (columns == null) {
			List<String> cols = new ArrayList(getCurrentPage().get(0)
					.getProperties().getKeys());
			Collections.sort(cols);
			cols.add(0,"_ID");
			columns = cols.toArray(new String[0]);
			for (int i=0; i< columns.length; i++) {

			}
		}
		return columns;
	}

	@Override
	public int getCount() {
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

	@Override
	public double getDouble(int column) {
		return getCurrentDocument().getDouble(columns[column]).floatValue();
	}

	@Override
	public float getFloat(int column) {
		return getCurrentDocument().getDouble(columns[column]).floatValue();
	}

	@Override
	public int getInt(int column) {
		if (column==0) {
			return getCurrentIdentifier().intValue();
		}
		return getCurrentDocument().getLong(columns[column]).intValue();
	}

	@Override
	public long getLong(int column) {
		if (column==0) {
			return getCurrentIdentifier();
		}
		return getCurrentDocument().getLong(columns[column]);
	}

	@Override
	public short getShort(int column) {
		return getCurrentDocument().getLong(columns[column])
				.shortValue();
	}

	@Override
	public String getString(int column) {
		if (column==0) {
			return getCurrentIdentifier().toString();
		}
		return getCurrentDocument().getString(columns[column]);
	}

	@Override
	public boolean isNull(int column) {
		return getCurrentDocument().getString(columns[column])==null;
	}

	@Override
	public void close() {
		super.close();
		for (Documents docs : pages.values()) {
			mapper.release(docs);
		}
	}

	@Override
	public Bundle getExtras() {
		Bundle bundle = new Bundle();
		Document doc = getCurrentDocument();
		Map<String, Object> data =  doc.getProperties().map();

		for (String k : data.keySet()) {
			Object val = data.get(k);
			if (val instanceof String) {
				bundle.putString(k, (String)val);
			} else if (val instanceof Boolean) {
				bundle.putBoolean(k, (Boolean)val);
			} else if (val instanceof Double) {
				bundle.putDouble(k, (Double)val);
			} else if (val instanceof Integer) {
				bundle.putInt(k, (Integer)val);
			} else if (val instanceof Long) {
				bundle.putLong(k, (Long)val);
			} else if (val instanceof PropertyList) {
				// XXX should do better
				PropertyList propList = (PropertyList) val;
				String[] stringArray = new String[propList.size()];
				for (int i = 0; i <propList.size(); i++) {
					stringArray[i] = propList.getString(i);
				}
				bundle.putStringArray(k, stringArray);
			}
		}
		return null;
	}

	public Document getDocument() {
		return getCurrentDocument();
	}
}
