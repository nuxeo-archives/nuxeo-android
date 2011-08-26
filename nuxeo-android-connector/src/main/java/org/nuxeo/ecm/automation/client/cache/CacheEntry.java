package org.nuxeo.ecm.automation.client.cache;

import java.io.InputStream;

import org.nuxeo.ecm.automation.client.jaxrs.spi.Request;
import org.nuxeo.ecm.automation.client.jaxrs.spi.Response;

public class CacheEntry {

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

    public CacheEntry(String ctype, String disp, InputStream is, Request request) {
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

    public CacheEntry(Request request, Response response) {
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


    /* (non-Javadoc)
	 * @see org.nuxeo.ecm.automation.client.cache.CallResult#getResponseStream()
	 */
    public InputStream getResponseStream() {
        return is;
    }

    /* (non-Javadoc)
	 * @see org.nuxeo.ecm.automation.client.cache.CallResult#setResponseStream(java.io.InputStream)
	 */
    public void setResponseStream(InputStream is) {
        this.is = is;
    }

    /* (non-Javadoc)
	 * @see org.nuxeo.ecm.automation.client.cache.CallResult#getReponseContentType()
	 */
    public String getReponseContentType() {
        return ctype;
    }

    /* (non-Javadoc)
	 * @see org.nuxeo.ecm.automation.client.cache.CallResult#setResponseContentType(java.lang.String)
	 */
    public void setResponseContentType(String ctype) {
        this.ctype = ctype;
    }

    /* (non-Javadoc)
	 * @see org.nuxeo.ecm.automation.client.cache.CallResult#getResponseContentDisposition()
	 */
    public String getResponseContentDisposition() {
        return disp;
    }

    /* (non-Javadoc)
	 * @see org.nuxeo.ecm.automation.client.cache.CallResult#setResponseContentDisposition(java.lang.String)
	 */
    public void setResponseContentDisposition(String disp) {
        this.disp = disp;
    }

    /* (non-Javadoc)
	 * @see org.nuxeo.ecm.automation.client.cache.CallResult#getRequestMethod()
	 */
    public int getRequestMethod() {
    	return rqMethod;
    }

    /* (non-Javadoc)
	 * @see org.nuxeo.ecm.automation.client.cache.CallResult#getRequestEntity()
	 */
    public String getRequestEntity() {
    	return rqEntity;
    }

	/* (non-Javadoc)
	 * @see org.nuxeo.ecm.automation.client.cache.CallResult#setRequestMethod(int)
	 */
	public void setRequestMethod(int rqMethod) {
		this.rqMethod = rqMethod;
	}

	/* (non-Javadoc)
	 * @see org.nuxeo.ecm.automation.client.cache.CallResult#setRequestEntity(java.lang.String)
	 */
	public void setRequestEntity(String rqEntity) {
		this.rqEntity = rqEntity;
	}




}
