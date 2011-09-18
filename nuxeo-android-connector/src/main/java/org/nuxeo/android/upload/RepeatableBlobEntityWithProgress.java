package org.nuxeo.android.upload;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import org.apache.http.entity.AbstractHttpEntity;
import org.nuxeo.ecm.automation.client.android.UIAsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;

public class RepeatableBlobEntityWithProgress extends AbstractHttpEntity {

	protected final static int BUFFER_SIZE = 2048;
	protected Blob blob;
	protected UIAsyncCallback<Serializable> cb;

	public RepeatableBlobEntityWithProgress (Blob blob, UIAsyncCallback<Serializable> cb) {
		this.blob=blob;
		this.cb=cb;
	}

	@Override
	public InputStream getContent() throws IOException, IllegalStateException {
		return new InputStreamWithProgress(blob.getStream(), blob.getLength(), cb);
	}

	@Override
	public long getContentLength() {
		return blob.getLength();
	}

	@Override
	public boolean isRepeatable() {
		return true;
	}

	@Override
	public boolean isStreaming() {
		return true;
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		 if (outstream == null) {
			    throw new IllegalArgumentException("Output stream may not be null");
			 }
		 InputStream instream = getContent();
		 byte[] buffer = new byte[BUFFER_SIZE];
		 int l;
		 long remaining = blob.getLength();
		 while (remaining > 0) {
		       l = instream.read(buffer, 0, (int)Math.min(BUFFER_SIZE, remaining));
		       if (l == -1) {
		           break;
		       }
		       outstream.write(buffer, 0, l);
		       remaining -= l;
		 }
	 }

}
