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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.spi.JsonMarshalling;
import org.nuxeo.ecm.automation.client.jaxrs.spi.Request;

public class CacheKeyHelper {

    private CacheKeyHelper() {
    }

    public static String getHash(String valueToHash) {
        MessageDigest digest;
        try {
            digest = java.security.MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        digest.update(valueToHash.getBytes());
        byte messageDigest[] = digest.digest();
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < messageDigest.length; i++) {
            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
        }
        return hexString.toString();
    }

    public static String getOperationDefinitionsCacheKey(String url) {
        String key = getHash(url) + "-automationDefinitions";
        return key;
    }

    public static String computeRequestKey(Request request) {

        String url = request.getUrl();
        if (url.endsWith("/login")) {
            // no caching
            return null;
        }

        if (url.endsWith("/automation/")) {
            // automation operation definitions
            return getOperationDefinitionsCacheKey(url);
        }

        StringBuffer sb = new StringBuffer();
        sb.append(request.getUrl());
        sb.append(request.asStringEntity());

        return getHash(sb.toString());
    }

    public static String computeRequestKey(OperationRequest request) {

        String url = request.getUrl();
        if (url.endsWith("/login")) {
            // no caching
            return null;
        }

        if (url.endsWith("/automation/")) {
            // automation operation definitions
            return getOperationDefinitionsCacheKey(url);
        }

        StringBuffer sb = new StringBuffer();
        sb.append(request.getUrl());
        try {
            sb.append(JsonMarshalling.writeRequest(request));
        } catch (Exception e1) {
            throw new RuntimeException("Unable to compute RequestKey", e1);
        }

        return getHash(sb.toString());
    }

}
