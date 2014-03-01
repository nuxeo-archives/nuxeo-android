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

import static org.nuxeo.ecm.automation.client.jaxrs.Constants.CTYPE_AUTOMATION;
import static org.nuxeo.ecm.automation.client.jaxrs.Constants.CTYPE_ENTITY;
import static org.nuxeo.ecm.automation.client.jaxrs.Constants.CTYPE_MULTIPART_MIXED;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nuxeo.ecm.automation.client.jaxrs.ConflictException;
import org.nuxeo.ecm.automation.client.jaxrs.RemoteException;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;
import org.nuxeo.ecm.automation.client.jaxrs.model.FileBlob;
import org.nuxeo.ecm.automation.client.jaxrs.model.StringBlob;
import org.nuxeo.ecm.automation.client.jaxrs.util.IOUtils;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class Request extends HashMap<String, String> {

    public static final int GET = 0;

    public static final int POST = 1;

    private static final long serialVersionUID = 1L;

    protected static Pattern ATTR_PATTERN = Pattern.compile(
            ";?\\s*filename\\s*=\\s*([^;]+)\\s*", Pattern.CASE_INSENSITIVE);

    protected final int method;

    protected final String url;

    protected final boolean isMultiPart;

    protected Object entity;

    protected Object result;

    protected boolean cachable = false;

    public Request(int method, String url) {
        this.method = method;
        this.url = url;
        isMultiPart = false;
    }

    public Request(int method, String url, String entity) {
        this.method = method;
        this.url = url;
        this.entity = entity;
        isMultiPart = false;
    }

    public int getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public Object getEntity() {
        return entity;
    }

    public final boolean isMultiPart() {
        return isMultiPart;
    }

    public String asStringEntity() {
        return isMultiPart ? null : (String) entity;
    }

    public Object getResult() {
        return result;
    }

    /**
     * Must read the object from the server response and return it or throw a
     * {@link RemoteException} if server sent an error.
     */
    public Object handleResult(int status, String ctype, String disp,
            InputStream stream) throws Exception {
        if (status == 204) { // no content
            return null;
        } else if (status >= 400) {
            handleException(status, ctype, stream);
        }
        if (ctype.startsWith(CTYPE_ENTITY)) {
            return JsonMarshalling.readEntity(IOUtils.read(stream));
        } else if (ctype.startsWith(CTYPE_AUTOMATION)) {
            return JsonMarshalling.readRegistry(IOUtils.read(stream));
        } else if (ctype.startsWith(CTYPE_MULTIPART_MIXED)) { // list of
                                                              // blobs
            throw new Exception("Multipart is not supported");
            // return readBlobs(ctype, stream);
        } else { // a blob?
            String fname = null;
            if (disp != null) {
                fname = getFileName(disp);
            }
            return readBlob(ctype, fname, stream);
        }
    }

    protected static Blob readBlob(String ctype, String fileName, InputStream in)
            throws Exception {
        if ("application/json".equals(ctype)) {
            StringBlob blob = new StringBlob(IOUtils.read(in));
            return blob;
        } else {
            File file = IOUtils.copyToTempFile(in);
            file.deleteOnExit();
            FileBlob blob = new FileBlob(file);
            blob.setMimeType(ctype);
            if (fileName != null) {
                blob.setFileName(fileName);
            }
            return blob;
        }
    }

    protected static String getFileName(String ctype) {
        Matcher m = ATTR_PATTERN.matcher(ctype);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    protected void handleException(int status, String ctype, InputStream stream)
            throws Exception {
        if (status == 409) {
            throw new ConflictException();
        }
        if (CTYPE_ENTITY.equals(ctype)) {
            String content = IOUtils.read(stream);
            RemoteException e = null;
            try {
                e = JsonMarshalling.readException(content);
            } catch (Throwable t) {
                throw new RemoteException(status, "ServerError",
                        "Server Error", content);
            }
            throw e;
        } else {
            throw new RemoteException(status, "ServerError", "Server Error",
                    IOUtils.read(stream));
        }
    }

}
