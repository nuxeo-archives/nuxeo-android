package org.nuxeo.android.context;

import org.nuxeo.android.config.NuxeoOfflineSettings;
import org.nuxeo.android.config.NuxeoServerConfig;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class NetworkStatusBroadCastReceiver extends BroadcastReceiver {

	protected final NuxeoServerConfig serverConfig;

	protected final NuxeoOfflineSettings offlineSettings;

	public NetworkStatusBroadCastReceiver(NuxeoServerConfig serverConfig, NuxeoOfflineSettings offlineSettings) {
		this.serverConfig = serverConfig;
		this.offlineSettings = offlineSettings;
	}

	@Override
	public void onReceive(Context ctx, Intent intent) {
		if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
			boolean reachable = false;
			int ip = serverConfig.getHostIP();
			if (ip>0) {
				// require perm
				//XXX reachable = cm.requestRouteToHost(ConnectivityManager.TYPE_MOBILE | ConnectivityManager.TYPE_WIFI, ip);
			}
			offlineSettings.setNetworkReachable(reachable);
		}
	}

}
