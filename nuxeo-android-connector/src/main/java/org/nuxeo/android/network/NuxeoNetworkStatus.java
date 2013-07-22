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

package org.nuxeo.android.network;

import java.net.HttpURLConnection;
import java.net.URL;

import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;
import org.nuxeo.android.config.NuxeoServerConfig;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;

public class NuxeoNetworkStatus extends BroadcastReceiver {

    private static final String TAG = "NuxeoNetworkStatus";

    protected final NuxeoServerConfig serverConfig;

    protected final ConnectivityManager cm;

    protected boolean forceOffline = false;

    protected boolean networkReachable = true;

    protected boolean nuxeoServerReachable = true;

    protected final Context androidContext;

    public NuxeoNetworkStatus(Context androidContext,
            NuxeoServerConfig serverConfig, ConnectivityManager cm) {
        this.androidContext = androidContext;
        this.serverConfig = serverConfig;
        this.cm = cm;
        androidContext.registerReceiver(this, new IntentFilter(
                NuxeoBroadcastMessages.NUXEO_SETTINGS_CHANGED));
        resetAsync();
    }

    public void resetAsync() {
        Runnable tester = new Runnable() {
            @Override
            public void run() {
                reset();
            }
        };
        new Thread(tester).start();
    }

    public void reset() {
        boolean hasNetwork = false;
        for (NetworkInfo netInfo : cm.getAllNetworkInfo()) {
            if (netInfo.getState() == State.CONNECTED) {
                int type = netInfo.getType();
                if (type != ConnectivityManager.TYPE_MOBILE_SUPL
                        || type != ConnectivityManager.TYPE_MOBILE_MMS) {
                    hasNetwork = true;
                    break;
                }
            }
        }
        if (hasNetwork) {
            networkReachable = true;
            testNuxeoServerReachable();
        } else {
            Log.d(TAG, "No network");
        }
    }

    public boolean isForceOffline() {
        return forceOffline;
    }

    public void setForceOffline(boolean forceOffline) {
        boolean recheck = this.forceOffline && !forceOffline;
        this.forceOffline = forceOffline;
        if (recheck) {
            resetAsync();
        }
    }

    public boolean isNetworkReachable() {
        return networkReachable;
    }

    public void setNetworkReachable(boolean networkReachable) {
        if (this.networkReachable != networkReachable) {
            this.networkReachable = networkReachable;
            if (!networkReachable) {
                setNuxeoServerReachable(false);
            }
            notifyChanged();
            Log.d(TAG, "Connectivity changed: networkReachable="
                    + networkReachable);
        }
    }

    public boolean canUseNetwork() {
        return networkReachable && nuxeoServerReachable && !forceOffline;
    }

    public boolean isNuxeoServerReachable() {
        return nuxeoServerReachable;
    }

    public void setNuxeoServerReachable(boolean nuxeoServerReachable) {
        if (this.nuxeoServerReachable != nuxeoServerReachable) {
            this.nuxeoServerReachable = nuxeoServerReachable;
            Log.d(TAG, "Connectivity changed: nuxeoServerReachable="
                    + nuxeoServerReachable);
            notifyChanged();
        }
    }

    public NuxeoServerConfig getServerConfig() {
        return serverConfig;
    }

    public boolean testNuxeoServerReachable() {
        setNuxeoServerReachable(pingNuxeoServer());
        return isNuxeoServerReachable();
    }

    protected boolean pingNuxeoServer() {
        try {
            URL url = new URL(getServerConfig().getServerBaseUrl()
                    + "login.jsp");
            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            urlc.setRequestProperty("User-Agent", "Nuxeo Android Application");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1000 * 15); // mTimeout is in seconds
            urlc.connect();
            if (urlc.getResponseCode() == 200) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Connection to Nuxeo server failed: " + e.getMessage());
            return false;
        }
    }

    public void notifyChanged() {
        androidContext.sendBroadcast(new Intent(
                NuxeoBroadcastMessages.NUXEO_SERVER_CONNECTIVITY_CHANGED));
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (intent.getAction().equals(
                NuxeoBroadcastMessages.NUXEO_SETTINGS_CHANGED)) {
            resetAsync();
        }
    }

}
