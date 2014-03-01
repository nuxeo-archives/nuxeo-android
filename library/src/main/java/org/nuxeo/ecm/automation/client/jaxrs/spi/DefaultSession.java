/*
 * (C) Copyright 2006-2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
package org.nuxeo.ecm.automation.client.jaxrs.spi;

import org.nuxeo.ecm.automation.client.broadcast.DocumentMessageService;
import org.nuxeo.ecm.automation.client.cache.OperationType;
import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.AutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.LoginInfo;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blobs;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.OperationDocumentation;
import org.nuxeo.ecm.automation.client.jaxrs.model.OperationInput;

import java.util.HashMap;
import java.util.Map;

import static org.nuxeo.ecm.automation.client.jaxrs.Constants.CTYPE_REQUEST_NOCHARSET;
import static org.nuxeo.ecm.automation.client.jaxrs.Constants.REQUEST_ACCEPT_HEADER;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * @author tiry
 */
public class DefaultSession implements Session {

    protected final AbstractAutomationClient client;

    protected final Connector connector;

    protected final LoginInfo login;

    public DefaultSession(AbstractAutomationClient client, Connector connector,
            LoginInfo login) {
        this.client = client;
        this.connector = connector;
        this.login = login;
    }

    @Override
    public AutomationClient getClient() {
        return client;
    }

    public Connector getConnector() {
        return connector;
    }

    @Override
    public LoginInfo getLogin() {
        return login;
    }

    @Override
    public <T> T getAdapter(Class<T> type) {
        return client.getAdapter(this, type);
    }

    @Override
    public Object execute(OperationRequest request) throws Exception {
        Request req;
        String content = JsonMarshalling.writeRequest(request);
        String ctype;
        OperationInput input = request.getInput();
        if (input != null && input.isBinary()) {
            throw new Exception("Binary request are not supported");
            // MultipartInput mpinput = new MultipartInput();
            // mpinput.setRequest(content);
            // ctype = mpinput.getContentType();
            // if (input instanceof Blob) {
            // Blob blob = (Blob) input;
            // mpinput.setBlob(blob);
            // } else if (input instanceof Blobs) {
            // mpinput.setBlobs((Blobs) input);
            // } else {
            // throw new IllegalArgumentException(
            // "Unsupported binary input object: " + input);
            // }
            // req = new Request(Request.POST, request.getUrl(), mpinput);
        } else {
            req = new Request(Request.POST, request.getUrl(), content);
            ctype = CTYPE_REQUEST_NOCHARSET;
        }
        // set headers
        for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
            req.put(entry.getKey(), entry.getValue());
        }
        req.put("Accept", REQUEST_ACCEPT_HEADER);
        req.put("Content-Type", ctype);

        // check offline settings
        boolean refresh = request.isForceRefresh() && !client.isOffline();
        Object result = connector.execute(req, refresh, request.isCachable());

        if (result != null && result instanceof Documents) {
            ((Documents) result).setSourceRequest(request);
        }
        return result;
    }

    @Override
    public String execute(final OperationRequest request,
            final AsyncCallback<Object> cb) {
        return client.asyncExec(this, request, cb);
    }

    @Override
    public Blob getFile(String path) throws Exception {
        Request req = new Request(Request.GET, client.getBaseUrl() + path);
        return (Blob) connector.execute(req);
    }

    @Override
    public Blobs getFiles(String path) throws Exception {
        Request req = new Request(Request.GET, client.getBaseUrl() + path);
        return (Blobs) connector.execute(req);
    }

    @Override
    public String getFile(final String path, final AsyncCallback<Blob> cb)
            throws Exception {
        final String requestKey = "file:" + path;
        client.asyncExec(new Runnable() {
            @Override
            public void run() {
                try {
                    cb.onSuccess(requestKey, getFile(path));
                } catch (Throwable t) {
                    cb.onError(requestKey, t);
                }
            }
        });
        return requestKey;
    }

    @Override
    public String getFiles(final String path, final AsyncCallback<Blobs> cb)
            throws Exception {
        final String requestKey = "files:" + path;
        client.asyncExec(new Runnable() {
            @Override
            public void run() {
                try {
                    cb.onSuccess(requestKey, getFiles(path));
                } catch (Throwable t) {
                    cb.onError(requestKey, t);
                }
            }
        });
        return requestKey;
    }

    @Override
    public OperationRequest newRequest(String id) {
        return newRequest(id, new HashMap<String, String>());
    }

    @Override
    public OperationRequest newRequest(String id, Map<String, String> ctx) {
        OperationDocumentation op = getOperation(id);
        if (op == null) {
            throw new IllegalArgumentException("No such operation: " + id);
        }
        return new DefaultOperationRequest(this, op, ctx);
    }

    @Override
    public OperationDocumentation getOperation(String id) {
        return client.getRegistry().getOperation(id);
    }

    @Override
    public Map<String, OperationDocumentation> getOperations() {
        return client.getRegistry().getOperations();
    }

    @Override
    public boolean isOffline() {
        return client.isOffline();
    }

    @Override
    public String execDeferredUpdate(OperationRequest request,
            AsyncCallback<Object> cb, OperationType opType) {
        return client.execDeferredUpdate(request, cb, opType);
    }

    @Override
    public DocumentMessageService getMessageHelper() {
        return client.getMessageHelper();
    }

}
