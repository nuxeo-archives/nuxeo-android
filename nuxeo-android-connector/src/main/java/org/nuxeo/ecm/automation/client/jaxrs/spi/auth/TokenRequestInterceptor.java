/*
 * (C) Copyright 2013 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Julien Carsique
 *
 */
package org.nuxeo.ecm.automation.client.jaxrs.spi.auth;

import org.apache.http.HttpRequest;
import org.nuxeo.ecm.automation.client.jaxrs.RequestInterceptor;
import org.nuxeo.ecm.automation.client.jaxrs.spi.Connector;
import org.nuxeo.ecm.automation.client.jaxrs.spi.Request;

import android.util.Log;

/**
 * 
 * For use with nuxeo-platform-login-token
 * 
 * @since 2.0
 */
public class TokenRequestInterceptor implements RequestInterceptor {
    private String token;

    private String login;

    private String deviceId;

    private String appName;

    public TokenRequestInterceptor(String appName, String token, String login,
            String deviceId) {
        this.token = token;
        this.login = login;
        this.deviceId = deviceId;
        this.appName = appName;
    }

    @Override
    public void processHttpRequest(HttpRequest request) {
        request.addHeader("X-User-Id", login);
        request.addHeader("X-Device-Id", deviceId);
        request.addHeader("X-Application-Name", appName);
        request.addHeader("X-Authentication-Token", token);
        Log.d("TokenRequestInterceptor", "processHttpRequest");
    }

    @Override
    public void processRequest(Request request, Connector connector) {
        request.put("X-User-Id", login);
        request.put("X-Device-Id", deviceId);
        request.put("X-Application-Name", appName);
        request.put("X-Authentication-Token", token);
        Log.d("TokenRequestInterceptor", "processRequest");

    }

}
