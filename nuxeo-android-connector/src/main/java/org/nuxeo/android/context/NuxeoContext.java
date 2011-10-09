package org.nuxeo.android.context;

import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;
import org.nuxeo.android.cache.blob.BlobStoreManager;
import org.nuxeo.android.cache.sql.SQLStateManager;
import org.nuxeo.android.config.NuxeoServerConfig;
import org.nuxeo.android.network.NetworkStatusBroadCastReceiver;
import org.nuxeo.android.network.NuxeoNetworkStatus;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.DisconnectedSession;
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

	protected final BlobStoreManager blobStore;

	public static NuxeoContext get(Context context) {
		if (context instanceof NuxeoContextProvider) {
			NuxeoContextProvider nxApp = (NuxeoContextProvider) context;
			return nxApp.getNuxeoContext();
		} else {
			throw new UnsupportedOperationException("Your application Context should implement NuxeoContextProvider !");
		}
	}

	public NuxeoContext(Context androidContext) {
		this.androidContext=androidContext;

		// persistence managers
		sqlStateManager = new SQLStateManager(androidContext);
		blobStore = new BlobStoreManager(androidContext);
		// config related services
		serverConfig = new NuxeoServerConfig(androidContext);
		networkStatus = new NuxeoNetworkStatus(androidContext, serverConfig, (ConnectivityManager) androidContext.getSystemService(Context.CONNECTIVITY_SERVICE));
		androidContext.registerReceiver(new NetworkStatusBroadCastReceiver(networkStatus), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

		// register this as listener for global config changes
		IntentFilter filter = new IntentFilter();
		filter.addAction(NuxeoBroadcastMessages.NUXEO_SETTINGS_CHANGED);
		filter.addAction(NuxeoBroadcastMessages.NUXEO_SERVER_CONNECTIVITY_CHANGED);
		androidContext.registerReceiver(this, filter);
		getNuxeoClient();
	}

	public NuxeoServerConfig getServerConfig() {
		return serverConfig;
	}

	public NuxeoNetworkStatus getNetworkStatus() {
		return networkStatus;
	}

	public synchronized Session getSession() {
		if (nuxeoSession!=null && nuxeoSession instanceof DisconnectedSession && networkStatus.canUseNetwork()) {
			nuxeoSession = null;
		}
		if (nuxeoSession==null) {
			nuxeoSession = getNuxeoClient().getSession(
					serverConfig.getLogin(),
					serverConfig.getPassword());
		}
		return nuxeoSession;
	}

	public DocumentManager getDocumentManager() {
		return getSession().getAdapter(DocumentManager.class);
	}

	protected void onConfigChanged() {
		nuxeoSession = null;
		nuxeoClient = null;
	}

	protected void onConnectivityChanged() {
		// NOP (Session automatically detects)
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
		if (nuxeoClient==null) {
			nuxeoClient = new AndroidAutomationClient(serverConfig.getAutomationUrl(), androidContext,sqlStateManager,blobStore,networkStatus,serverConfig);
		}
		return nuxeoClient;
	}
}
