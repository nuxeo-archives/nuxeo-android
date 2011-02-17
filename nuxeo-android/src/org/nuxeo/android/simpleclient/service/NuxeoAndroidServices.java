/*
 * (C) Copyright 2010-2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 */

package org.nuxeo.android.simpleclient.service;

import org.nuxeo.android.simpleclient.Constants;

import com.smartnsoft.droid4me.ws.WebServiceCaller;

/**
 * A single point of access to the web services.
 *
 * @author Nuxeo & Smart&Soft
 * @since 2011.02.17
 */
public final class NuxeoAndroidServices extends WebServiceCaller {

    private static volatile NuxeoAndroidServices instance;

    // We accept the "out-of-order writes" case
    public static NuxeoAndroidServices getInstance() {
        if (instance == null) {
            synchronized (NuxeoAndroidServices.class) {
                if (instance == null) {
                    instance = new NuxeoAndroidServices();
                }
            }
        }
        return instance;
    }

    private NuxeoAndroidServices() {
    }

    @Override
    protected String getUrlEncoding() {
        return Constants.WEBSERVICES_HTML_ENCODING;
    }

}
