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
package org.nuxeo.ecm.automation.client.jaxrs;

import java.util.Map;

import org.nuxeo.ecm.automation.client.broadcast.DocumentMessageService;
import org.nuxeo.ecm.automation.client.cache.OperationType;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blobs;
import org.nuxeo.ecm.automation.client.jaxrs.model.OperationDocumentation;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * @author tiry
 */
public interface Session {

    /**
     * Get the client that created this session.
     * 
     * @return the client. cannot be null.
     */
    AutomationClient getClient();

    /**
     * Get the login used to authenticate against the server
     * 
     * @return the login. cannot be null.
     */
    LoginInfo getLogin();

    /**
     * Create a new operation request given an operation ID.
     * 
     * @param id the ID of the operation to be executed.
     * @return the operation request
     */
    OperationRequest newRequest(String id);

    /**
     * Create a new operation request given an operation ID and an operation
     * context map.
     * 
     * @param id the operation id
     * @param ctx the context map to be used when executing the operation on
     *            the server.
     * @return the operation request
     */
    OperationRequest newRequest(String id, Map<String, String> ctx)
            throws Exception;

    Object execute(OperationRequest request) throws Exception;

    String execute(OperationRequest request, AsyncCallback<Object> cb);

    /**
     * Get a file from the server given a path identifying the file.
     * 
     * @param path the file path
     * @return a blob representation of the file
     */
    Blob getFile(String path) throws Exception;

    /**
     * Get a collection of files from the server given the path identifying the
     * collection.
     * 
     * @param path the file path
     * @return a collection of files represented as blobs.
     */
    Blobs getFiles(String path) throws Exception;

    String getFile(String path, AsyncCallback<Blob> cb) throws Exception;

    String getFiles(String path, AsyncCallback<Blobs> cb) throws Exception;

    OperationDocumentation getOperation(String id);

    Map<String, OperationDocumentation> getOperations();

    /**
     * Get an adapter of the current session. Adapters can be used to define
     * custom API over a Nuxeo Automation Session.
     * <p>
     * Optional operation. Environments that cannot support this method (like
     * GWT) must throw {@link UnsupportedOperationException}
     * 
     * @see AutomationClient#getAdapter(Object, Class)
     */
    <T> T getAdapter(Class<T> type);

    boolean isOffline();

    String execDeferredUpdate(OperationRequest request,
            AsyncCallback<Object> cb, OperationType opType);

    DocumentMessageService getMessageHelper();

}
