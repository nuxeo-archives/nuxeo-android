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

package org.nuxeo.ecm.automation.client.cache;

import java.io.InputStream;

import org.nuxeo.ecm.automation.client.jaxrs.spi.Request;
import org.nuxeo.ecm.automation.client.jaxrs.spi.Response;

public class ResponseCacheEntry {

    /**
     * Stream of the response
     */
    protected InputStream is;

    /**
     * Content Type of the response
     */
    protected String ctype;

    /**
     * Content Disposition of the response
     */
    protected String disp;

    protected int rqMethod;

    protected String rqEntity;

    public ResponseCacheEntry(String ctype, String disp, InputStream is, Request request) {
        this.is = is;
        this.ctype=ctype;
        this.disp = disp;

        if (request!=null) {
        	rqMethod = request.getMethod();
        	if (request.getEntity()!=null) {
        		rqEntity = request.getEntity().toString();
        	}
        }
    }

    public ResponseCacheEntry(Request request, Response response) {
        this.is = response.getInputStream();
        this.ctype=response.getCtype();
        this.disp = response.getDisp();

        if (request!=null) {
        	rqMethod = request.getMethod();
        	if (request.getEntity()!=null) {
        		rqEntity = request.getEntity().toString();
        	}
        }
    }

    public InputStream getResponseStream() {
        return is;
    }

    public void setResponseStream(InputStream is) {
        this.is = is;
    }

    public String getReponseContentType() {
        return ctype;
    }

    public void setResponseContentType(String ctype) {
        this.ctype = ctype;
    }

    public String getResponseContentDisposition() {
        return disp;
    }

    public void setResponseContentDisposition(String disp) {
        this.disp = disp;
    }

    public int getRequestMethod() {
    	return rqMethod;
    }

    public String getRequestEntity() {
    	return rqEntity;
    }

	public void setRequestMethod(int rqMethod) {
		this.rqMethod = rqMethod;
	}

	public void setRequestEntity(String rqEntity) {
		this.rqEntity = rqEntity;
	}
}
