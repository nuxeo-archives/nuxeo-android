package org.nuxeo.android.activities;

import org.nuxeo.android.context.NuxeoContext;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.Session;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

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
}
