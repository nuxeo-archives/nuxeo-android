package org.nuxeo.android.adapters;

import java.util.Map;

import org.nuxeo.android.documentprovider.LazyDocumentsList;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public class DocumentsListAdapter extends AbstractDocumentListAdapter implements ListAdapter {

	protected final DocumentViewBinder binder;

	public DocumentsListAdapter(Context context, LazyDocumentsList docList, int layoutId, Map<Integer, String> documentAttributesMapping) {
		this(context, docList, new SimpleDocumentViewBinder(layoutId, documentAttributesMapping));
	}

	public DocumentsListAdapter(Context context, LazyDocumentsList docList, DocumentViewBinder binder) {
		super(context, docList);
		this.binder = binder;
	}

	protected View createNewView(int position, Document doc, LayoutInflater inflater, ViewGroup parent) {
		return binder.createNewView(position, doc, inflater, parent);
	}

	protected void bindViewToDocument(int position, Document doc, View view) {
		binder.bindViewToDocument(position, doc, view);
	}

}
