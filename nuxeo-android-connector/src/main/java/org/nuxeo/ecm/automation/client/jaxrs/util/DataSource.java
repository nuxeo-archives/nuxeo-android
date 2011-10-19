package org.nuxeo.ecm.automation.client.jaxrs.util;

public abstract interface DataSource {

    public abstract java.io.InputStream getInputStream()
            throws java.io.IOException;

    public abstract java.io.OutputStream getOutputStream()
            throws java.io.IOException;

    public abstract java.lang.String getContentType();

    public abstract java.lang.String getName();
}