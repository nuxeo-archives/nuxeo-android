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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;

public class NetworkStatusBroadCastReceiver extends BroadcastReceiver {

    protected final NuxeoNetworkStatus networkStatus;

    public NetworkStatusBroadCastReceiver(NuxeoNetworkStatus offlineSettings) {
        this.networkStatus = offlineSettings;
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

            Bundle bundle = intent.getExtras();

            if (bundle.getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY)) {
                networkStatus.setNetworkReachable(false);
            } else {
                String reason = bundle.getString(ConnectivityManager.EXTRA_REASON);
                boolean isFailover = bundle.getBoolean(
                        ConnectivityManager.EXTRA_IS_FAILOVER, false);
                NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

                boolean connectivityOk = isNetworkUsable(currentNetworkInfo);
                if (!connectivityOk && otherNetworkInfo != null) {
                    connectivityOk = isNetworkUsable(otherNetworkInfo);
                }

                if (!connectivityOk) {
                    networkStatus.setNetworkReachable(false);
                } else {
                    networkStatus.setNetworkReachable(true);
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            networkStatus.testNuxeoServerReachable();
                            return null;
                        }
                    };
                }
            }
        }
    }

    protected boolean isNetworkUsable(NetworkInfo networkInfo) {
        int type = networkInfo.getType();
        if (type == ConnectivityManager.TYPE_MOBILE_MMS)
            return false;
        if (type == ConnectivityManager.TYPE_MOBILE_SUPL)
            return false;
        return networkInfo.getState() == State.CONNECTED;

    }

}
