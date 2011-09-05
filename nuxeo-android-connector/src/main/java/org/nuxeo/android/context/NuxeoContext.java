package org.nuxeo.android.context;

import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;
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

/**
 *
 * @author tiry
 *
 */
public class NuxeoContext extends BroadcastReceiver {

	protected static NuxeoContext instance = null;

	protected NuxeoServerConfig serverConfig;

	protected NuxeoNetworkStatus networkStatus;

	protected AndroidAutomationClient nuxeoClient;

	protected Session nuxeoSession;

    protected final SQLStateManager sqlStateManager;

	protected final Context androidContext;

	public static NuxeoContext get(Context context) {
		if (context instanceof NuxeoContextProvider) {
			NuxeoContextProvider nxApp = (NuxeoContextProvider) context;
			return nxApp.getNuxeoContext();
		} else {
			throw new UnsupportedOperationException("Your application Context should implement NuxeoContextProvider");
		}
	}

	public NuxeoContext(Context androidContext) {
		this.androidContext=androidContext;
		sqlStateManager = new SQLStateManager(androidContext);
		serverConfig = new NuxeoServerConfig(androidContext);
		networkStatus = new NuxeoNetworkStatus(androidContext, serverConfig, (ConnectivityManager) androidContext.getSystemService(Context.CONNECTIVITY_SERVICE));
		androidContext.registerReceiver(new NetworkStatusBroadCastReceiver(networkStatus), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		// register this as listener
		IntentFilter filter = new IntentFilter();
		filter.addAction(NuxeoBroadcastMessages.NUXEO_SETTINGS_CHANGED);
		filter.addAction(NuxeoBroadcastMessages.NUXEO_SERVER_CONNECTIVITY_CHANGED);
		androidContext.registerReceiver(this, filter);
	}

	public NuxeoServerConfig getServerConfig() {
		return serverConfig;
	}

	public NuxeoNetworkStatus getNetworkStatus() {
		return networkStatus;
	}

	public synchronized Session getSession() {
		if (nuxeoSession==null) {
			nuxeoClient = new AndroidAutomationClient(serverConfig.getAutomationUrl(), androidContext,sqlStateManager,networkStatus);
			nuxeoSession = nuxeoClient.getSession(
					serverConfig.getLogin(),
					serverConfig.getPassword());
		}
		return nuxeoSession;
	}

	public DocumentManager getDocumentManager() {
		return new DocumentManager(getSession());
	}

	protected void onConfigChanged() {
		nuxeoSession = null;
		nuxeoClient = null;
	}

	protected void onConnectivityChanged() {
		// NOP (Session automatically detects
	}

	@Override
	public void onReceive(Context ctx, Intent intent) {
		if (intent.getAction().equals(NuxeoBroadcastMessages.NUXEO_SETTINGS_CHANGED)) {
			onConfigChanged();
		}
		else if (intent.getAction().equals(NuxeoBroadcastMessages.NUXEO_SERVER_CONNECTIVITY_CHANGED)) {
			onConnectivityChanged();
		}
	}

	public AndroidAutomationClient getNuxeoClient() {
		return nuxeoClient;
	}
}
