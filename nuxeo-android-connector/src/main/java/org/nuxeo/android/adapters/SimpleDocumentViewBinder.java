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

import java.text.SimpleDateFormat;
import java.util.Date;
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

	protected static SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");

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
			if (attribute.startsWith(DocumentsListAdapter.DATE_PREIX)) {
				Date date = DocumentAttributeResolver.getDate(doc, attribute.substring(DocumentsListAdapter.DATE_PREIX.length()));
				if (date!=null) {
					((TextView)widget).setText(fmt.format(date));
				}
			} else {
				((TextView)widget).setText(DocumentAttributeResolver.getString(doc, attribute));
			}

		}
		else if (widget instanceof ImageView) {
			((ImageView)widget).setImageURI((Uri) DocumentAttributeResolver.get(doc, attribute));
		}
	}

}
