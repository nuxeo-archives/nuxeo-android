package org.nuxeo.android.cache.blob;

import java.io.File;
import java.util.Properties;

import org.nuxeo.ecm.automation.client.jaxrs.model.FileBlob;

public class BlobWithProperties extends FileBlob {

	protected final Properties properties;

	public BlobWithProperties(File file, String filename, String mimeType, Properties properties) {
		super(file, filename, mimeType);
		this.properties=properties;
	}

	public BlobWithProperties(File file, Properties properties) {
		super(file);
		this.properties=properties;
	}

	public String getProperty(String name) {
		if (properties!=null) {
			return properties.getProperty(name);
		} else {
			return null;
		}
	}

}
