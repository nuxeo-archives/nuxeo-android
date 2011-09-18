package org.nuxeo.android.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Intent;
import android.view.ViewGroup;

public class NuxeoLayout implements ActivityResultHandler {

	protected List<NuxeoWidget> widgets = new ArrayList<NuxeoWidget>();

	protected final ViewGroup view;

	protected Document doc;

	public NuxeoLayout(ViewGroup view, Document doc) {
		this.view = view;
		this.doc = doc;
	}

	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {

		for (NuxeoWidget widget : widgets) {
			Map<Integer, ActivityResultHandler> handlers = widget.getAndFlushPendingActivityResultHandler();
			if (handlers!=null) {
				ActivityResultHandler handler = handlers.get(requestCode);
				if (handler!=null) {
					handler.onActivityResult(requestCode, resultCode, data);
					return true;
				}
			}
		}
		return false;
	}

	public ViewGroup getContainer() {
		return view;
	}

	void addWidgets(List<NuxeoWidget> newwidgets) {
		widgets.addAll(newwidgets);
	}



	public void applyChanges(Document doc) {
		this.doc = doc;
		for (NuxeoWidget widget : widgets) {
			widget.applyChanges(doc);
		}
	}

	public void refreshFromDocument(Document doc) {
		this.doc = doc;
		for (NuxeoWidget widget : widgets) {
			widget.refresh(doc);
		}
	}

}
