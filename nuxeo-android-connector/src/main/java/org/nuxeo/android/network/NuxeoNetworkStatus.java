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

public class NuxeoNetworkStatus extends BroadcastReceiver{

	protected final NuxeoServerConfig serverConfig;

	protected final ConnectivityManager cm;

	protected boolean forceOffline = false;

	protected boolean networkReachable = true;

	protected boolean nuxeoServerReachable = true;

	protected final Context androidContext;

	public NuxeoNetworkStatus(Context androidContext, NuxeoServerConfig serverConfig, ConnectivityManager cm) {
		this.androidContext=androidContext;
		this.serverConfig = serverConfig;
		this.cm = cm;
		androidContext.registerReceiver(this, new IntentFilter(NuxeoBroadcastMessages.NUXEO_SETTINGS_CHANGED));
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
			if (netInfo.getState()==State.CONNECTED) {
				 int type = netInfo.getType();
				 if (type != ConnectivityManager.TYPE_MOBILE_SUPL || type != ConnectivityManager.TYPE_MOBILE_MMS) {
					 hasNetwork = true;
					 break;
				 }
				}
		}
		if (hasNetwork) {
			networkReachable = true;
			testNuxeoServerReachable();
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
		this.networkReachable = networkReachable;
		if (!networkReachable) {
			setNuxeoServerReachable(false);
		} else {
			notifyChanged();
		}
	}

	public boolean canUseNetwork() {
		return networkReachable && nuxeoServerReachable && !forceOffline;
	}

	public boolean isNuxeoServerReachable() {
		return nuxeoServerReachable;
	}

	public void setNuxeoServerReachable(boolean nuxeoServerReachable) {
		this.nuxeoServerReachable = nuxeoServerReachable;
		notifyChanged();
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
			URL url = new URL(getServerConfig().getServerBaseUrl() + "login.jsp");
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
			return false;
		}
	}

	public void notifyChanged() {
		androidContext.sendBroadcast(new Intent(NuxeoBroadcastMessages.NUXEO_SERVER_CONNECTIVITY_CHANGED));
	}

	@Override
	public void onReceive(Context ctx, Intent intent) {
		if (intent.getAction().equals(NuxeoBroadcastMessages.NUXEO_SETTINGS_CHANGED)) {
			resetAsync();
		}
	}

}
