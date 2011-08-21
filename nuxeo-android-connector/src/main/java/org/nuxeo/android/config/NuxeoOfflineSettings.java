package org.nuxeo.android.config;

public class NuxeoOfflineSettings {

	protected boolean forceOffline = false;

	protected boolean networkReachable = true;

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
	}

}
