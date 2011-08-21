package org.nuxeo.android.context;

import org.nuxeo.android.cache.DefaultCacheManager;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

public class SimpleNuxeoApplication extends Application implements NuxeoContextProvider {

	protected NuxeoContext nuxeoContext=null;

	public NuxeoContext getNuxeoContext() {
		if (nuxeoContext==null) {
			nuxeoContext = new NuxeoContext();
			nuxeoContext.setCacheManager(new DefaultCacheManager(this));
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			nuxeoContext.setConnectivityManager(cm);
			registerReceiver(nuxeoContext.getNetworkStatusBroadCastReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		}
		return nuxeoContext;
	}
}
