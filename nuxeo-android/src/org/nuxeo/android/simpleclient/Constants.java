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

package org.nuxeo.android.simpleclient;

import org.apache.http.protocol.HTTP;

import android.util.Log;

/**
 * Gathers in one place the constants of the application.
 * 
 * @author Nuxeo & Smart&Soft
 * @since 2011.02.17
 */
public abstract class Constants {

    /**
     * The logging level of the application and of the droid4me framework.
     */
    public static final int LOG_LEVEL = Log.DEBUG;

    /**
     * The e-mail that will receive error reports.
     */
    public static final String REPORT_LOG_RECIPIENT_EMAIL = "android@smartnsoft.com";

    /**
     * The encoding used for wrapping the URL of the HTTP requests.
     */
    public static final String WEBSERVICES_HTML_ENCODING = HTTP.ISO_8859_1;

}
