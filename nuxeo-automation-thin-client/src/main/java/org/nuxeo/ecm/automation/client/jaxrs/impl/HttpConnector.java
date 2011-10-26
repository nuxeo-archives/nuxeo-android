/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     bstefanescu
 */
package org.nuxeo.ecm.automation.client.jaxrs.impl;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.nuxeo.ecm.automation.client.cache.CacheEntry;
import org.nuxeo.ecm.automation.client.cache.InputStreamCacheManager;
import org.nuxeo.ecm.automation.client.jaxrs.RemoteException;
import org.nuxeo.ecm.automation.client.jaxrs.spi.Connector;
import org.nuxeo.ecm.automation.client.jaxrs.spi.Request;

/**
 * Connector wrapping a {@link HttpClient} instance.
 * 
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class HttpConnector implements Connector {

    protected final AbstractHttpClient http;

    protected final HttpContext ctx;

    protected String basicAuth;

    protected InputStreamCacheManager cacheManager;

    public void setCacheManager(InputStreamCacheManager manager) {
        cacheManager = manager;
    }

    public HttpConnector(HttpClient http) {
        this(http, new BasicHttpContext());
    }

    public HttpConnector(HttpClient http, HttpContext ctx) {
        ctx.setAttribute(ClientContext.COOKIE_STORE, new BasicCookieStore());
        this.http = (AbstractHttpClient) http;
        this.ctx = ctx;
    }

    protected String computeRequestKey(Request request) {

        String url = request.getUrl();
        if (url.endsWith("/login")) {
            // no caching
            return null;
        }

        if (url.endsWith("/automation/")) {
            // automation operation definitions
            return "automationDefinitions";
        }

        StringBuffer sb = new StringBuffer();
        sb.append(request.getUrl());
        sb.append(request.asStringEntity());

        MessageDigest digest;
        try {
            digest = java.security.MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        digest.update(sb.toString().getBytes());
        byte messageDigest[] = digest.digest();
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < messageDigest.length; i++)
            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
        return hexString.toString();
    }

    @Override
    public Object execute(Request request) {
        return execute(request, false, true);
    }

    @Override
    public Object execute(Request request, boolean forceRefresh,
            boolean cachable) {

        String cacheKey = null;
        CacheEntry cachedResult = null;
        if (cacheManager != null) {
            cacheKey = computeRequestKey(request);
            cachedResult = cacheManager.getFromCache(cacheKey);
            if (cachedResult != null && !forceRefresh) {
                System.out.println("Cache HIT");
                try {
                    return request.handleResult(200, cachedResult.getCtype(),
                            cachedResult.getDisp(),
                            cachedResult.getInputStream());
                } catch (Exception e) {
                    // NOP
                }
            }
        }

        if (!cachable) {
            // desable caching if needed
            cacheKey = null;
        }

        HttpRequestBase httpRequest = null;
        if (request.getMethod() == Request.POST) {
            HttpPost post = new HttpPost(request.getUrl());
            Object obj = request.getEntity();
            if (obj != null) {
                HttpEntity entity = null;
                if (request.isMultiPart()) {
                    throw new RuntimeException("MultiPart is not supported");
                } else {
                    try {
                        entity = new StringEntity(obj.toString(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        throw new Error("Cannot encode into UTF-8", e);
                    }
                }
                post.setEntity(entity);
            }
            httpRequest = post;
        } else {
            httpRequest = new HttpGet(request.getUrl());
        }
        try {
            return execute(request, httpRequest, cacheKey);
        } catch (RemoteException e) {
            if (cachedResult != null) {
                try {
                    return request.handleResult(200, cachedResult.getCtype(),
                            cachedResult.getDisp(),
                            cachedResult.getInputStream());
                } catch (Exception e2) {
                    throw e;
                }
            } else {
                throw e;
            }
        } catch (Throwable t) {
            if (isNetworkError(t)) {
                if (cachedResult != null) {
                    try {
                        return request.handleResult(200,
                                cachedResult.getCtype(),
                                cachedResult.getDisp(),
                                cachedResult.getInputStream());
                    } catch (Throwable t2) {
                        throw new NotAvailableOffline(
                                "Can not fetch result from cache", t2);
                    }
                } else {
                    throw new NotAvailableOffline(
                            "No data in cache, must be online");
                }
            } else {
                throw new RuntimeException("Cannot execute " + request, t);
            }
        }
    }

    protected boolean isNetworkError(Throwable t) {
        String className = t.getClass().getName();

        if (className.startsWith("java.net.")) {
            return true;
        }
        if (className.startsWith("org.apache.http.conn.")) {
            return true;
        }

        return false;
    }

    protected Object execute(Request request, HttpUriRequest httpReq,
            String cacheKey) throws Exception {
        for (Map.Entry<String, String> entry : request.entrySet()) {
            httpReq.setHeader(entry.getKey(), entry.getValue());
        }
        HttpResponse resp = http.execute(httpReq, ctx);
        HttpEntity entity = resp.getEntity();
        int status = resp.getStatusLine().getStatusCode();
        if (entity == null) {
            if (status < 400) {
                return null;
            }
            throw new RemoteException(status, "ServerError", "Server Error",
                    null);
        }
        Header ctypeHeader = entity.getContentType();
        if (ctypeHeader == null) { // handle broken responses with no ctype
            if (status != 200) {
                // this may happen when login failed
                throw new RemoteException(status, "ServerError",
                        "Server Error", null);
            }
            return null; // cannot handle responses with no ctype
        }
        String ctype = ctypeHeader.getValue().toLowerCase();
        String disp = null;
        Header[] hdisp = resp.getHeaders("Content-Disposition");
        if (hdisp != null && hdisp.length > 0) {
            disp = hdisp[0].getValue();
        }

        InputStream is = entity.getContent();
        if (cacheKey != null && cacheManager != null && status == 200) {
            // store in cache
            is = cacheManager.addToCache(cacheKey, new CacheEntry(ctype, disp,
                    is));
        }
        return request.handleResult(status, ctype, disp, is);
    }

}
