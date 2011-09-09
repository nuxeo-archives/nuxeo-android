package org.nuxeo.android.activities;

import java.io.File;

import org.nuxeo.android.context.NuxeoContext;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.Session;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class BaseNuxeoActivity extends Activity {

	protected NuxeoContext getNuxeoContext() {
		return NuxeoContext.get(getApplicationContext());
	}

	protected Session getNuxeoSession() {
		return getNuxeoContext().getSession();
	}

	protected AndroidAutomationClient getAutomationClient() {
		return getNuxeoContext().getNuxeoClient();
	}

	protected class NuxeoAsyncTask extends AsyncTask<Void, Integer, Object> {

		@Override
		protected void onPreExecute() {
			onNuxeoDataRetrievalStarted();
			super.onPreExecute();
		}

		@Override
		protected Object doInBackground(Void... arg0) {
			try {
				Object result = retrieveNuxeoData();
				return result;
			} catch (Exception e) {
				Log.e("NuxeoAsyncTask", "Error while executing async Nuxeo task in activity", e);
				cancel(true);
				return null;
			}
		}

		@Override
		protected void onPostExecute(Object result) {
			if (result!=null) {
				onNuxeoDataRetrieved(result);
			}
		}
	}

	protected void runAsyncDataRetrieval() {
		new NuxeoAsyncTask().execute((Void[])null);
	}

	protected Object retrieveNuxeoData() throws Exception {
		return null;
	}

	protected void onNuxeoDataRetrievalStarted() {
	}

	protected void onNuxeoDataRetrieved(Object data) {

	}

    protected void startViewerFromBlob(File tmpFile, String mimeTye) {
        Uri path = Uri.fromFile(tmpFile);
        startViewerFromBlob(path, mimeTye);
    }

    protected void startViewerFromBlob(Uri uri, String mimeTye) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mimeTye);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            startActivity(intent);
        }
        catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(this,
                "No Application Available to View " + mimeTye,
                Toast.LENGTH_SHORT).show();
        }
    }

}
