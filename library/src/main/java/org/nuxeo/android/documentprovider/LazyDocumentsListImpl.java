/*
 * (C) Copyright 2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 */

package org.nuxeo.android.documentprovider;

import android.net.Uri;
import android.util.Log;

import org.nuxeo.android.contentprovider.NuxeoContentProviderConfig;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.broadcast.DocumentMessageService;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.CacheKeyHelper;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class LazyDocumentsListImpl implements LazyDocumentsList {

    private static final double PREFETCH_TRIGGER = 0.5;

    protected Map<Integer, Documents> pages = new ConcurrentHashMap<Integer, Documents>();

    protected CopyOnWriteArrayList<String> loadingInProgress = new CopyOnWriteArrayList<String>();

    protected int currentPosition = 0;

    protected int currentPage = 0;

    protected int pageCount = 0;

    protected int pageSize = 20;

    protected int totalSize = 0;

    protected String name;

    protected final Session session;

    protected OperationRequest fetchOperation;

    protected final String pageParameterName;

    protected String exposedMimeType;

    protected List<DocumentsListChangeListener> listeners = new ArrayList<DocumentsListChangeListener>();

    public LazyDocumentsListImpl(Session session, String nxql,
            String[] queryParams, String sortOrder, String schemas, int pageSize) {

        pages = new ConcurrentHashMap<Integer, Documents>();

        // XXX sort order !
        this.pageSize = pageSize;
        this.currentPage = 0;
        this.session = session;
        this.pageParameterName = "page";

        fetchOperation = session.newRequest("Document.PageProvider").set(
                "query", nxql).set("pageSize", pageSize).set(pageParameterName,
                0);
        if (queryParams != null) {
            fetchOperation.set("queryParams", queryParams);
        }
        // define returned properties
        if (schemas == null) {
            schemas = "common,dublincore";
        }
        fetchOperation.setHeader("X-NXDocumentProperties", schemas);
        this.name = nxql;
        // fetchPageSync(currentPage);
        fetchPageAsync(currentPage, false);
    }

    public LazyDocumentsListImpl(OperationRequest fetchOperation,
            String pageParametrerName) {

        pages = new ConcurrentHashMap<Integer, Documents>();

        this.pageParameterName = pageParametrerName;
        this.currentPage = 0;
        this.session = fetchOperation.getSession();
        this.fetchOperation = fetchOperation;
        // fetchPageSync(currentPage);
        fetchPageAsync(currentPage, false);
    }

    protected AndroidAutomationClient getClient() {
        return (AndroidAutomationClient) session.getClient();
    }

    protected DocumentMessageService getMessageHelper() {
        return getClient().getMessageHelper();
    }

    @Override
    public Documents getFirstPage() {
        return pages.get(0);
    }

    @Override
    public Documents getCurrentPage() {
        return pages.get(currentPage);
    }

    @Override
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

    protected Documents afterPageFetch(int pageIdx, Documents docs) {
        return docs;
    }

    protected Documents fetchPageSync(int targetPage) {
        if (pages.containsKey(targetPage)) {
            return pages.get(targetPage);
        }

        Log.w(this.getClass().getSimpleName(),
                "WARN -- sync fetching of new page -- " + targetPage
                        + " ... this is very wrong !!!");

        Documents docs = null;
        if (loadingInProgress.addIfAbsent("" + targetPage)) {
            docs = queryDocuments(targetPage, CacheBehavior.STORE, null);
            docs = afterPageFetch(targetPage, docs);
            pages.put(targetPage, docs);
            notifyContentChanged(targetPage);
            return docs;
        } else {
            // block until loading completed !!!
            while (loadingInProgress.contains("" + targetPage)) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // NOP
                }
            }
            return fetchPageSync(targetPage);
        }
    }

    protected void fetchPageAsync(final int targetPage, boolean refresh) {
        if (pages.containsKey(targetPage) && !refresh) {
            return;
        }
        if (loadingInProgress.addIfAbsent("" + targetPage)) {
            byte cacheSetting = CacheBehavior.STORE;
            if (refresh) {
                cacheSetting = (byte) (cacheSetting | CacheBehavior.FORCE_REFRESH);
            }
            queryDocuments(targetPage, cacheSetting,
                    new AsyncCallback<Object>() {
                        @Override
                        public void onError(String executionId, Throwable e) {
                            Log.e(LazyDocumentsListImpl.class.getSimpleName(),
                                    "Error during async page fetching", e);
                            loadingInProgress.remove("" + targetPage);
                        }

                        @Override
                        public void onSuccess(String executionId, Object data) {
                            Log.i(LazyDocumentsListImpl.class.getSimpleName(),
                                    "Page fetch successful");
                            Documents docs = (Documents) data;
                            pageSize = docs.getPageSize();
                            docs = afterPageFetch(targetPage, docs);
                            loadingInProgress.remove("" + targetPage);
                            pages.put(targetPage, docs);
                            setPageCount(docs.getPageCount());
                            totalSize = docs.getTotalSize();
                            notifyContentChanged(targetPage);
                        }
                    });
        }
    }

    protected void setPageCount(int pc) {
        pageCount = pc;
        if (pageCount < pages.size()) {
            // remove old pages that may correspond to document that were
            // deleted
            for (int i = pageCount; i < pages.size(); i++) {
                pages.remove(i);
            }
            notifyContentChanged(-1);
        }
    }

    @Override
    public void refreshAll() {
        for (Integer idx : pages.keySet()) {
            fetchPageAsync(idx, true);
        }
    }

    protected void notifyContentChanged(int page) {
        for (DocumentsListChangeListener listener : listeners) {
            listener.notifyContentChanged(page);
        }
    }

    protected Documents queryDocuments(int page, byte cacheFlags,
            AsyncCallback<Object> cb) {
        Documents docs;
        try {
            if (cb == null) {
                if (pageParameterName != null) {
                    fetchOperation.set(pageParameterName, page);
                }
                docs = (Documents) fetchOperation.execute(cacheFlags);
                pageSize = docs.getPageSize();
                setPageCount(docs.getPageCount());
                loadingInProgress.remove("" + page);
                totalSize = docs.getTotalSize();
                return docs;
            } else {
                OperationRequest asyncRequest = fetchOperation.clone();
                if (pageParameterName != null) {
                    asyncRequest.set(pageParameterName, page);
                }
                asyncRequest.execute(cb, cacheFlags);
                return null;
            }
        } catch (Exception e) {
            Log.e(LazyDocumentsListImpl.class.getSimpleName(),
                    "Error while fetching documents", e);
            return null;
        }
    }

    protected int computeTargetPage(int position) {
        return position / pageSize;
    }

    @Override
    public int getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public int setCurrentPosition(int position) {

        int targetPageIndex = computeTargetPage(position);
        if (targetPageIndex >= pageCount) {
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
        return globalPosition - pageIndex * pageSize;
    }

    protected int getRelativePositionOnPage() {
        int pos = getCurrentPosition();
        int targetPageIndex = computeTargetPage(pos);
        return getRelativePositionOnPage(pos, targetPageIndex);
    }

    protected void prefetchIfNeeded(int pos) {
        if (pos > PREFETCH_TRIGGER * pageSize) {
            final int pageToFetch = currentPage + 1;
            if (pageToFetch < pageCount) {
                if (!pages.containsKey(pageToFetch)) {
                    fetchPageAsync(pageToFetch, false);
                }
            }
        }
    }

    @Override
    public Document getCurrentDocument() {
        int pos = getRelativePositionOnPage();
        Documents currentDocs = getCurrentPage();

        if (currentDocs.size() > pos) {
            return currentDocs.get(pos);
        } else {
            int tryCount = 0;
            // Yurk !!!
            while (tryCount < 5) {
                currentDocs = getCurrentPage();
                if (currentDocs.size() > pos) {
                    return currentDocs.get(pos);
                } else {
                    try {
                        Thread.currentThread();
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                tryCount++;
            }
            Log.e(LazyDocumentsListImpl.class.getSimpleName(),
                    "global position : " + getCurrentPosition());
            Log.e(LazyDocumentsListImpl.class.getSimpleName(), "total size : "
                    + getCurrentSize());
            Log.e(LazyDocumentsListImpl.class.getSimpleName(),
                    "current page idex : " + currentPage);
            Log.e(LazyDocumentsListImpl.class.getSimpleName(),
                    "current page size : " + currentDocs.size());
            throw new UnsupportedOperationException("Wrong index");
        }
    }

    @Override
    public int getPageCount() {
        return pageCount;
    }

    @Override
    public int getLoadedPageCount() {
        return pages.size();
    }

    @Override
    public Integer getLoadingPagesCount() {
        return loadingInProgress.size();
    }

    @Override
    public Iterator<Document> getIterator() {
        return new Iterator<Document>() {

            @Override
            public boolean hasNext() {
                return currentPosition < totalSize;
            }

            @Override
            public Document next() {
                setCurrentPosition(currentPosition + 1);
                return getCurrentDocument();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException(
                        "Can not remove Document from the iterator");
            }
        };
    }

    @Override
    public Collection<Documents> getLoadedPages() {
        return pages.values();
    }

    @Override
    public boolean isFullyLoaded() {
        return pages.size() == pageCount;
    }

    @Override
    public int getCurrentSize() {
        if (getCurrentPage() == null) {
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
    public void registerListener(DocumentsListChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unregisterListener(DocumentsListChangeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public Document getDocument(int index) {

        int targetPage = computeTargetPage(index);
        int offset = index - targetPage * pageSize;

        if (pages.containsKey(targetPage)) {
            return pages.get(targetPage).get(offset);
        }
        return null;
    }

    @Override
    public String getName() {
        if (name == null) {
            OperationRequest rq = fetchOperation.clone();
            if (pageParameterName != null) {
                rq.set(pageParameterName, 0);
            }
            name = CacheKeyHelper.computeRequestKey(rq);
        }
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public OperationRequest getFetchOperation() {
        return fetchOperation;
    }

    @Override
    public String getPageParameterName() {
        return pageParameterName;
    }

    @Override
    public String getExposedMimeType() {
        return exposedMimeType;
    }

    @Override
    public void setExposedMimeType(String exposedMimeType) {
        this.exposedMimeType = exposedMimeType;

    }

    @Override
    public Uri getContentUri() {
        return Uri.parse("content://"
                + NuxeoContentProviderConfig.getAuthority() + "/" + getName());
    }

}
