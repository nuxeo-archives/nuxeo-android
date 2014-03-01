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

package org.nuxeo.android.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.nuxeo.android.documentprovider.DocumentsListChangeListener;
import org.nuxeo.android.documentprovider.LazyDocumentsList;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

public abstract class AbstractDocumentListAdapter extends BaseAdapter {

    protected LayoutInflater inflater;

    protected final LazyDocumentsList docList;

    protected int currentCount = -1;

    protected final UUIDMapper mapper;

    protected Handler handler;

    protected final Integer loadingLayout;

    protected View loadingView;

    public AbstractDocumentListAdapter(Context context,
            LazyDocumentsList docList) {
        this(context, docList, null);
    }

    public AbstractDocumentListAdapter(Context context,
            LazyDocumentsList docList, Integer loadingLayout) {
        super();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.docList = docList;
        this.mapper = new UUIDMapper();
        this.loadingLayout = loadingLayout;
        registerEventListener();
    }

    protected void registerEventListener() {
        // enforce UI Thread for the List resfresh : even if this seems strange
        // to have to do this ...
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                currentCount = -1;
                notifyDataSetChanged();
            }
        };

        docList.registerListener(new DocumentsListChangeListener() {
            @Override
            public void notifyContentChanged(int page) {
                handler.sendEmptyMessage(page);
            }
        });
    }

    protected boolean useLoadingView() {
        return loadingLayout != null && !docList.isFullyLoaded();
    }

    @Override
    public int getCount() {
        if (currentCount < 0) {
            currentCount = docList.getCurrentSize();
        }
        if (useLoadingView()) {
            return currentCount + 1;
        } else {
            return currentCount;
        }
    }

    public int getRealCount() {
        if (currentCount < 0) {
            return docList.getCurrentSize();
        }
        return currentCount;
    }

    public Object getDocumentStatus(Integer position) {
        Document doc = getDocument(position);
        if (doc == null) {
            return null;
        }
        return doc.getStatusFlag().toString();
    }

    public Object getDocumentAttribute(Integer position, String attributeName) {
        Document doc = getDocument(position);
        if (doc == null) {
            Log.i(this.getClass().getSimpleName(),
                    "No document found in list for position " + position);
            return null;
        }
        Log.i(this.getClass().getSimpleName(),
                "document found in list at position " + position
                        + ", returning attibute " + attributeName);
        return doc.getProperties().get(attributeName);
    }

    public int getFirstPageCount() {
        if (docList == null || docList.getPageCount() == 0) {
            return 0;
        }
        return docList.getFirstPage().size();
    }

    protected Document getDocument(int position) {
        // XXX use this for now to be sure to trigger lazy fetch
        // => to be replaced by docList.getDocument(position);
        docList.setCurrentPosition(position);
        return docList.getCurrentDocument();
    }

    @Override
    public Object getItem(int position) {
        return getDocument(position);
    }

    @Override
    public long getItemId(int position) {
        return mapper.getIdentifier(getDocument(position));
    }

    protected View getLoadingView(ViewGroup parent) {
        if (loadingView == null) {
            loadingView = inflater.inflate(loadingLayout, parent, false);
        }
        return loadingView;
    }

    @Override
    public View getView(int position, View recycledView, ViewGroup parent) {

        int realCount = getRealCount();
        boolean showLoading = useLoadingView();

        if ((position >= realCount) && showLoading) {
            return getLoadingView(parent);
        }

        Document doc = getDocument(position);
        if (recycledView == null || recycledView == getLoadingView(parent)) {
        	recycledView = createNewView(position, doc, inflater, parent);
        }
        bindViewToDocument(position, doc, recycledView);
        return recycledView;
    }

    protected abstract View createNewView(int position, Document doc,
            LayoutInflater inflater, ViewGroup parent);

    protected abstract void bindViewToDocument(int position, Document doc,
            View view);

}