package org.nuxeo.android.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
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
				boolean isFailover = bundle.getBoolean(ConnectivityManager.EXTRA_IS_FAILOVER, false);
				NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
				NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

				boolean connectivityOk = isNetworkUsable(currentNetworkInfo);
				if (!connectivityOk && otherNetworkInfo!=null) {
					connectivityOk = isNetworkUsable(otherNetworkInfo);
				}

				if (!connectivityOk) {
					networkStatus.setNetworkReachable(false);
				} else {
					networkStatus.setNetworkReachable(true);
					networkStatus.testNuxeoServerReachable();
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
		return networkInfo.getState()== State.CONNECTED;

	}

}
