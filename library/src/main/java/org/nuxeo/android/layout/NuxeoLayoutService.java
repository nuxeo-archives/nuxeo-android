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

package org.nuxeo.android.layout;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;

import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

public interface NuxeoLayoutService {

    NuxeoLayout getLayout(Fragment fragment, Document doc, ViewGroup parent,
            LayoutMode mode);

    NuxeoLayout parseLayoutDefinition(String definition, Fragment fragment,
            Document doc, ViewGroup parent, LayoutMode mode);

	NuxeoLayout getLayout(Fragment fragment, Document doc, ViewGroup parent,
			LayoutMode mode, String layoutName);

    NuxeoLayout parseLayoutDefinition(String definition, Activity ctx,
            Document doc, ViewGroup parent, LayoutMode mode);

    NuxeoLayout getLayout(Activity ctx, Document doc, ViewGroup parent,
            LayoutMode mode);

    NuxeoLayout getLayout(Activity ctx, Document doc, ViewGroup parent,
            LayoutMode mode, String layoutName);

}
