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

package org.nuxeo.android.context;

import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;
import org.nuxeo.android.cache.blob.BlobStoreManager;
import org.nuxeo.android.cache.sql.SQLStateManager;
import org.nuxeo.android.config.NuxeoServerConfig;
import org.nuxeo.android.network.NetworkStatusBroadCastReceiver;
import org.nuxeo.android.network.NuxeoNetworkStatus;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.Session;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 *
 * @author tiry
 *
 */
public class NuxeoContext extends BroadcastReceiver {

    private static final String TAG = "NuxeoContext";

    protected static NuxeoContext instance = null;

    protected NuxeoServerConfig serverConfig;

    protected NuxeoNetworkStatus networkStatus;

    protected AndroidAutomationClient nuxeoClient;

    protected final SQLStateManager sqlStateManager;

    protected final Context androidContext;

    protected final BlobStoreManager blobStore;

    private boolean shuttingDown;

    /**
     * @param nxContextProvider the application context. Must implement
     *            {@link NuxeoContextProvider}
     * @return the nuxeoContext associated with the application
     * @see android.app.Application#getApplicationContext()
     * @see SimpleNuxeoApplication
     */
    public static NuxeoContext get(Context nxContextProvider) {
        if (nxContextProvider instanceof NuxeoContextProvider) {
            NuxeoContextProvider nxApp = (NuxeoContextProvider) nxContextProvider;
            return nxApp.getNuxeoContext();
        } else {
            throw new UnsupportedOperationException(
                    "Your application Context should implement NuxeoContextProvider !");
        }
    }

    public NuxeoContext(Context androidContext) {
        this.androidContext = androidContext;

        // persistence managers
        sqlStateManager = new SQLStateManager(androidContext);
        blobStore = new BlobStoreManager(androidContext);
        // config related services
        serverConfig = new NuxeoServerConfig(androidContext);
        networkStatus = new NuxeoNetworkStatus(
                androidContext,
                serverConfig,
                (ConnectivityManager) androidContext.getSystemService(Context.CONNECTIVITY_SERVICE));
        androidContext.registerReceiver(new NetworkStatusBroadCastReceiver(
                networkStatus), new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));

        // register this as listener for global config changes
        IntentFilter filter = new IntentFilter();
        filter.addAction(NuxeoBroadcastMessages.NUXEO_SETTINGS_CHANGED);
        filter.addAction(NuxeoBroadcastMessages.NUXEO_SERVER_CONNECTIVITY_CHANGED);
        androidContext.registerReceiver(this, filter);
        getNuxeoClient();
    }

    public NuxeoServerConfig getServerConfig() {
        return serverConfig;
    }

    public NuxeoNetworkStatus getNetworkStatus() {
        return networkStatus;
    }

    public synchronized Session getSession() {
        return getNuxeoClient().getSession();
    }

    public DocumentManager getDocumentManager() {
        return getSession().getAdapter(DocumentManager.class);
    }

    public synchronized void onConfigChanged() {
        Log.d(TAG, "onConfigChanged");
        if (nuxeoClient != null) {
            Log.d(TAG, "shutdown " + nuxeoClient);
            shuttingDown = true;
            nuxeoClient.dropCurrentSession();
            nuxeoClient.shutdown();
            nuxeoClient = null;
            notify();
        }
    }

    protected void onConnectivityChanged() {
        // NOP (Session automatically detects)
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (intent.getAction().equals(
                NuxeoBroadcastMessages.NUXEO_SETTINGS_CHANGED)) {
            onConfigChanged();
        } else if (intent.getAction().equals(
                NuxeoBroadcastMessages.NUXEO_SERVER_CONNECTIVITY_CHANGED)) {
            onConnectivityChanged();
        }
    }

    public synchronized AndroidAutomationClient getNuxeoClient() {
        while (nuxeoClient != null && shuttingDown) {
            try {
                wait();
            } catch (InterruptedException e) {
                // Do nothing
            }
        }
        if (nuxeoClient == null || nuxeoClient.isShutdown()) {
            shuttingDown = false;
            nuxeoClient = new AndroidAutomationClient(
                    serverConfig.getAutomationUrl(), androidContext,
                    sqlStateManager, blobStore, networkStatus, serverConfig);
            Log.d(TAG, "new " + nuxeoClient);
        }
        return nuxeoClient;
    }
}
