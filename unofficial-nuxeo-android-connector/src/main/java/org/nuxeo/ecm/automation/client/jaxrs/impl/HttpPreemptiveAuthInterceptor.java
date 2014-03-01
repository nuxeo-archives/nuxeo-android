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

package org.nuxeo.ecm.automation.client.jaxrs.impl;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

public class HttpPreemptiveAuthInterceptor implements HttpRequestInterceptor {

    protected final AuthScheme authScheme;

    HttpPreemptiveAuthInterceptor(AuthScheme authScheme) {
        this.authScheme = authScheme;
    }

    @Override
    public void process(HttpRequest request, HttpContext context)
            throws HttpException, IOException {
        AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
        // If not auth scheme has been initialized yet
        if (authState.getAuthScheme() != null) {
            return;
        }

        // fetch credentials
        CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(ClientContext.CREDS_PROVIDER);
        HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
        AuthScope authScope = new AuthScope(targetHost.getHostName(),
                targetHost.getPort());

        // Obtain credentials matching the target host
        Credentials creds = credsProvider.getCredentials(authScope);

        if (creds == null) {
            System.out.println("no credentials provided for " + authScope);
            // log.warn("no credentials provided for " + authScope);
            return;
        }

        authState.setAuthScheme(authScheme);
        authState.setCredentials(creds);
    }

}
