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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import org.nuxeo.android.documentprovider.LazyDocumentsList;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import java.util.Map;

public class DocumentsListAdapter extends AbstractDocumentListAdapter implements
		ListAdapter {

	protected final DocumentViewBinder binder;

	public static final String DATE_PREIX = "(date)";

	@Deprecated
	/**
	 * @deprecated Since 2.0. prefer #DocumentsListAdapter(Context context, LazyDocumentsList docList,
	        int layoutId, Map<Integer, String> documentAttributesMapping)
	        loading layout is useless and it costs more to convert it than to use the target layout directly
	 */
	public DocumentsListAdapter(Context context, LazyDocumentsList docList,
			int layoutId, Map<Integer, String> documentAttributesMapping,
			Integer loadingLayout) {
		this(context, docList, new SimpleDocumentViewBinder(layoutId,
				documentAttributesMapping), loadingLayout);
	}

	public DocumentsListAdapter(Context context, LazyDocumentsList docList,
			int layoutId, Map<Integer, String> documentAttributesMapping) {
		this(context, docList, new SimpleDocumentViewBinder(layoutId,
				documentAttributesMapping), layoutId);
	}

	public DocumentsListAdapter(Context context, LazyDocumentsList docList,
			DocumentViewBinder binder, Integer loadingLayout) {
		super(context, docList, loadingLayout);
		this.binder = binder;
	}

    @Override
    protected View createNewView(int position, Document doc,
            LayoutInflater inflater, ViewGroup parent) {
        return binder.createNewView(position, doc, inflater, parent);
    }

    @Override
    protected void bindViewToDocument(int position, Document doc, View view) {
        binder.bindViewToDocument(position, doc, view);
    }

}
