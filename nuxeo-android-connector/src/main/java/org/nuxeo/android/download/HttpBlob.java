package org.nuxeo.android.download;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;

public class HttpBlob extends Blob {

	protected HttpResponse response;

	public HttpBlob(HttpResponse response) {
		super("file", "application/octet-stream");
		this.response=response;
		Header ct = response.getFirstHeader("Content-Type");
		if (ct!=null) {
			this.mimeType= ct.getValue();
		}

		Header cd = response.getFirstHeader("Content-Disposition");
		if (cd!=null) {
			HeaderElement[] elements = cd.getElements();
			if (elements.length>0) {
				NameValuePair fname = elements[0].getParameterByName("filename");
				if (fname!=null) {
					this.fileName = fname.getValue();
				}
			}
		}
	}

	@Override
	public InputStream getStream() throws IOException {
		return response.getEntity().getContent();
	}

}
