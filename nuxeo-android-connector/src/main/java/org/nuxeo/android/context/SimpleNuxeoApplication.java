package org.nuxeo.android.context;

import org.nuxeo.android.cache.DefaultResponseCacheManager;
import org.nuxeo.android.pending.DefaultDeferedUpdateManager;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;

public class SimpleNuxeoApplication extends Application implements NuxeoContextProvider {

	protected NuxeoContext nuxeoContext=null;

	public NuxeoContext getNuxeoContext() {
		if (nuxeoContext==null) {
			nuxeoContext = new NuxeoContext();
			nuxeoContext.setCacheManager(new DefaultResponseCacheManager(this));
			nuxeoContext.setDeferredUpdateManager(new DefaultDeferedUpdateManager(this));
			nuxeoContext.setDeferredUpdateManager(new DefaultDeferedUpdateManager(this));
			nuxeoContext.setSharedPrefs(PreferenceManager.getDefaultSharedPreferences(this));
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			nuxeoContext.setConnectivityManager(cm);
			registerReceiver(nuxeoContext.getNetworkStatusBroadCastReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		}
		return nuxeoContext;
	}
}
