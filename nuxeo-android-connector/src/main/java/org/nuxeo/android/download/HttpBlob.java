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

package org.nuxeo.android.download;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;

public class HttpBlob extends Blob {

    protected HttpResponse response;

    public HttpBlob(HttpResponse response) {
        super("file", "application/octet-stream");
        this.response = response;
        Header ct = response.getFirstHeader("Content-Type");
        if (ct != null) {
            this.mimeType = ct.getValue();
        }

        Header cd = response.getFirstHeader("Content-Disposition");
        if (cd != null) {
            HeaderElement[] elements = cd.getElements();
            if (elements.length > 0) {
                NameValuePair fname = elements[0].getParameterByName("filename");
                if (fname != null) {
                    this.fileName = fname.getValue();
                }
            }
        }
    }

    @Override
    public InputStream getStream() throws IOException {
        return response.getEntity().getContent();
    }

}
