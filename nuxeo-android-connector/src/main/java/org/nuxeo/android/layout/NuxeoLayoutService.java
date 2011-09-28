package org.nuxeo.android.layout;

import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.app.Activity;
import android.view.ViewGroup;

public interface NuxeoLayoutService {

	NuxeoLayout parseLayoutDefinition(String definition, Activity ctx, Document doc, ViewGroup parent, LayoutMode mode);

	NuxeoLayout getLayout(Activity ctx, Document doc, ViewGroup parent, LayoutMode mode);

	NuxeoLayout getLayout(Activity ctx, Document doc, ViewGroup parent, LayoutMode mode, String layoutName);

}
