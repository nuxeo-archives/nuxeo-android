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
package org.nuxeo.ecm.automation.client.jaxrs.spi;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.nuxeo.ecm.automation.client.jaxrs.RequestInterceptor;

public class ConnectorHandler implements Connector {

    protected final Connector connector;

    protected final RequestInterceptor interceptor;

    public ConnectorHandler(Connector connector, RequestInterceptor interceptor) {
        this.connector = connector;
        this.interceptor = interceptor;
    }

    @Override
    public Object execute(Request request) {
        return execute(request, false, true);
    }

    @Override
    public Object execute(Request request, boolean forceRefresh,
            boolean cachable) {
        interceptor.processRequest(request, connector);
        return connector.execute(request, forceRefresh, cachable);
    }

    @Override
    public HttpResponse executeSimpleHttp(HttpUriRequest httpRequest)
            throws Exception {
        interceptor.processHttpRequest(httpRequest);
        return connector.executeSimpleHttp(httpRequest);
    }

}
