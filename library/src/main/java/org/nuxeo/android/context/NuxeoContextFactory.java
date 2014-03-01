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

package org.nuxeo.android.context;

import android.content.Context;

import org.nuxeo.android.config.NuxeoServerConfig;

public class NuxeoContextFactory {

    protected static NuxeoContext nuxeoContext = null;

    public static NuxeoContext getNuxeoContext(Context context) {
        if (nuxeoContext == null) {
            nuxeoContext = new NuxeoContext(context);
        }
        return nuxeoContext;
    }

    /**
     * @since 2.0
     */
    public static NuxeoContext getNuxeoContext(Context context,
            NuxeoServerConfig nxConfig) {
        if (nuxeoContext == null) {
            nuxeoContext = new NuxeoContext(context, nxConfig);
        }
        return nuxeoContext;
    }

}
