package org.nuxeo.ecm.automation.client.jaxrs.spi;

import java.io.InputStream;

public class Response {

	protected int status;
	protected String ctype;
	protected String disp;
	protected InputStream is;

	public Response(int status, String ctype, String disp, InputStream is) {
		this.status = status;
		this.ctype = ctype;
		this.disp = disp;
		this.is = is;
	}

	public int getStatus() {
		return status;
	}

	public String getCtype() {
		return ctype;
	}

	public String getDisp() {
		return disp;
	}

	public InputStream getInputStream() {
		return is;
	}

	public Object getResult(Request request) throws Exception {
		return request.handleResult(status, ctype, disp, is);
	}

	public void setInputStream(InputStream is) {
		this.is = is;
	}


}
