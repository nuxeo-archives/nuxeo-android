/*
 * (C) Copyright 2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     Nuxeo - initial API and implementation
 */

package org.nuxeo.android.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.nuxeo.android.context.NuxeoContext;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.NotAvailableOffline;

import java.io.File;

public abstract class BaseNuxeoFragment extends Fragment {

    protected boolean loadingInProgress = false;

    protected NuxeoContext getNuxeoContext() {
        return NuxeoContext.get(getActivity().getApplicationContext());
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
            loadingInProgress = true;
            onNuxeoDataRetrievalStarted();
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Void... arg0) {
            try {
                Object result = retrieveNuxeoData();
                return result;
            } catch (NotAvailableOffline naoe) {
            	BaseNuxeoFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getBaseContext(),
                                "This screen can bot be displayed offline",
                                Toast.LENGTH_LONG).show();
                    }
                });
                return null;
            } catch (Exception e) {
                Log.e("NuxeoAsyncTask",
                        "Error while executing async Nuxeo task in activity", e);
                try {
                    cancel(true);
                } catch (Throwable t) {
                    // NOP
                }
                return null;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            loadingInProgress = false;
            if (result != null) {
                onNuxeoDataRetrieved(result);
            } else {
                onNuxeoDataRetrieveFailed();
            }
        }
    }

    protected abstract boolean requireAsyncDataRetrieval();

    @Override
    public void onResume() {
        super.onResume();
        if (requireAsyncDataRetrieval()) {
            runAsyncDataRetrieval();
        }
    }

    protected void runAsyncDataRetrieval() {
        new NuxeoAsyncTask().execute((Void[]) null);
    }

    /**
     * Should be overridden to include Async process.
     * Returning a null result will cancel the callback
     */
    protected Object retrieveNuxeoData() throws Exception {
        return null;
    }

    /**
     * Called on the UI Thread to notify that async process is started
     * This may be used to display a waiting message
     */
    protected void onNuxeoDataRetrievalStarted() {
    }

    /**
     * Called on the UI Thread when the async process is completed.
     * The input object will be the output of the retrieveNuxeoData
     */
    protected void onNuxeoDataRetrieved(Object data) {

    }

    /**
     * Called on the UI Thread when the async process is completed in error.
     */
    protected void onNuxeoDataRetrieveFailed() {

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
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(getActivity().getBaseContext(), "No Application Available to View " + mimeTye,
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void startViewerFromBlob(Uri uri) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(getActivity().getBaseContext(),
                    "No Application Available to View uri " + uri.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            try {
                ((ViewGroup) view).removeAllViews();
            } catch (Throwable e) {
                // NOP
            }
        }
    }

    protected <T> T getInitParam(String name, Class<T> type) {
        if (getActivity().getIntent().getExtras() != null) {
            Object value = getActivity().getIntent().getExtras().get(name);
            if (value != null) {
                return type.cast(value);
            }
        }
        return null;
    }
    
    protected <T> T getInitFragmentParam(String name, Class<T> type) {
    	if (getArguments() != null) {
    		Object value = getArguments().get(name);
    		if (value != null){
                return type.cast(value);
            }
        }
        return null;
    }

    public boolean isReady() {
        return !loadingInProgress;
    }

}