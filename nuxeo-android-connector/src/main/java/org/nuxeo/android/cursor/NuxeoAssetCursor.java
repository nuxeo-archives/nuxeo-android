package org.nuxeo.android.cursor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.nuxeo.ecm.automation.client.android.AndroidResponseCacheManager;
import org.nuxeo.ecm.automation.client.cache.StreamHelper;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;

import android.content.Context;
import android.database.AbstractCursor;
import android.util.Log;

public class NuxeoAssetCursor extends AbstractCursor {

	private String[] columnNames = {"_ID", "_data"};

	protected String fileName;

	public NuxeoAssetCursor(Session session, Context androidContext) {
		HttpUriRequest request = new HttpGet("http://10.0.2.2:8080/nuxeo/icons/file.gif");
		try {
			HttpResponse response = ((HttpAutomationClient) session.getClient()).http().execute(request);
			File cacheDir = androidContext.getExternalCacheDir();
			if (cacheDir==null) {
				Log.w(AndroidResponseCacheManager.class.getSimpleName(), "No external directory accessible, using main storage");
				cacheDir = androidContext.getFilesDir();
			}
			fileName = System.currentTimeMillis() + "fileTest.gif";
			File streamFile = new File(cacheDir, fileName);
			InputStream is = response.getEntity().getContent();
			try {
				FileOutputStream out = new FileOutputStream(streamFile);
				StreamHelper.copy(is, out);
				is.close();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String[] getColumnNames() {
		return columnNames;
	}

	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public double getDouble(int arg0) {
        throw new UnsupportedOperationException();
	}

	@Override
	public float getFloat(int arg0) {
        throw new UnsupportedOperationException();
    }

	@Override
	public int getInt(int arg0) {
        throw new UnsupportedOperationException();
	}

	@Override
	public long getLong(int arg0) {
        throw new UnsupportedOperationException();
    }

	@Override
	public short getShort(int arg0) {
        throw new UnsupportedOperationException();
    }

	@Override
	public String getString(int idx) {
		if (idx==0) {
			return "0";
		} else if (idx==1) {
			return fileName;
		}
		return null;
	}

	@Override
	public boolean isNull(int arg0) {
		return false;
	}

}
