package org.nuxeo.android.adapters;

import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface DocumentViewBinder {

	View createNewView(int position, Document doc, LayoutInflater inflater, ViewGroup parent);

	void bindViewToDocument(int position, Document doc, View view);

}
