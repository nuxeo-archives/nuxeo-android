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

import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface DocumentViewBinder {

	View createNewView(int position, Document doc, LayoutInflater inflater, ViewGroup parent);

	void bindViewToDocument(int position, Document doc, View view);

}
