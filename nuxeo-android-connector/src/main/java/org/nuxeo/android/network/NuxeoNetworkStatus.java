package org.nuxeo.android.network;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.nuxeo.android.config.NuxeoServerConfig;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Handler;

public class NuxeoNetworkStatus {

	protected final NuxeoServerConfig serverConfig;

	protected final ConnectivityManager cm;

	protected boolean forceOffline = false;

	protected boolean networkReachable = true;

	protected boolean nuxeoServerReachable = true;

	protected List<Handler> handlers = new ArrayList<Handler>();

	public NuxeoNetworkStatus(NuxeoServerConfig serverConfig, ConnectivityManager cm) {
		this.serverConfig = serverConfig;
		this.cm = cm;
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

	public void notifyChanged() {
		for (Handler handler : handlers) {
			handler.sendEmptyMessage(0);
		}
	}

	public boolean isForceOffline() {
		return forceOffline;
	}

	public void setForceOffline(boolean forceOffline) {
		this.forceOffline = forceOffline;
	}

	public boolean isNetworkReachable() {
		return networkReachable;
	}

	public void setNetworkReachable(boolean networkReachable) {
		this.networkReachable = networkReachable;
		if (!networkReachable) {
			this.nuxeoServerReachable = false;
		}
		notifyChanged();
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

	public void registerHandler(Handler handler) {
		handlers.add(handler);
	}

	public void unregisterHandler(Handler handler) {
		handlers.remove(handler);
	}

}
