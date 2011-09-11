package org.nuxeo.android.adapters;

import java.util.Map;

import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SimpleDocumentViewBinder implements DocumentViewBinder {

	protected final int layoutId;
	protected final Map<Integer, String> documentAttributesMapping;

	public SimpleDocumentViewBinder(int layoutId, Map<Integer, String> documentAttributesMapping) {
		this.layoutId = layoutId;
		this.documentAttributesMapping = documentAttributesMapping;
	}

	@Override
	public void bindViewToDocument(int position, Document doc, View view) {
		for (Integer idx : documentAttributesMapping.keySet()) {
			View widget = view.findViewById(idx);
			bindWidgetToDocumentAttribute(widget, doc, documentAttributesMapping.get(idx));
		}
	}

	@Override
	public View createNewView(int position, Document doc, LayoutInflater inflater, ViewGroup parent) {
		return inflater.inflate(layoutId, parent,false);
	}

	protected void bindWidgetToDocumentAttribute(View widget, Document doc, String attribute) {
		if (widget instanceof TextView) {
			((TextView)widget).setText(DocumentAttributeResolver.getString(doc, attribute));
		}
		else if (widget instanceof ImageView) {
			Uri uri = (Uri) DocumentAttributeResolver.get(doc, attribute);
			((ImageView)widget).setImageURI((Uri) DocumentAttributeResolver.get(doc, attribute));
		}
	}

}
