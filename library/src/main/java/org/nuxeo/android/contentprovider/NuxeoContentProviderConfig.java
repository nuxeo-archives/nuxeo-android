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
package org.nuxeo.android.contentprovider;

import android.content.pm.ProviderInfo;

public class NuxeoContentProviderConfig {

    public static String DEFAULT_AUTHORITY = "nuxeo";

    public static String PROVIDER_NAME = "org.nuxeo.android.contentprovider.NuxeoDocumentContentProvider";

    protected static String authority = null;

    protected static ProviderInfo pinfo;

    public static void init(ProviderInfo info) {
        pinfo = info;
    }

    public static String getAuthority() {
        if (authority == null) {
            authority = pinfo.authority;
        }
        if (authority == null) {
            return DEFAULT_AUTHORITY;
        }
        return authority;
    }

}
