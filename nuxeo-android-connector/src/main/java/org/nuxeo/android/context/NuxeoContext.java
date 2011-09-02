package org.nuxeo.android.context;

import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;
import org.nuxeo.android.cache.DefaultDeferedUpdateManager;
import org.nuxeo.android.cache.DefaultResponseCacheManager;
import org.nuxeo.android.cache.sql.SQLStateManager;
import org.nuxeo.android.config.NuxeoServerConfig;
import org.nuxeo.android.network.NetworkStatusBroadCastReceiver;
import org.nuxeo.android.network.NuxeoNetworkStatus;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.cache.CacheAwareHttpAutomationClient;
import org.nuxeo.ecm.automation.client.cache.DeferredUpdateManager;
import org.nuxeo.ecm.automation.client.cache.ResponseCacheManager;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;

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

	protected HttpAutomationClient nuxeoClient;

	protected ResponseCacheManager responseCacheManager;

	protected DeferredUpdateManager deferredUpdateManager;

	protected Session nuxeoSession;

	protected final Context androidContext;

	protected SQLStateManager sqlStateManager;

	public static NuxeoContext get(Context context) {
		if (context instanceof NuxeoContextProvider) {
			NuxeoContextProvider nxApp = (NuxeoContextProvider) context;
			return nxApp.getNuxeoContext();
		} else {
			if (instance==null) {
				instance = new NuxeoContext(context);
				// XXX should we allow that ???
			}
			return instance;
		}
	}

	public NuxeoContext(Context androidContext) {
		this.androidContext=androidContext;
		serverConfig = new NuxeoServerConfig(androidContext);
		networkStatus = new NuxeoNetworkStatus(androidContext, serverConfig, (ConnectivityManager) androidContext.getSystemService(Context.CONNECTIVITY_SERVICE));
		androidContext.registerReceiver(new NetworkStatusBroadCastReceiver(networkStatus), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		// register this as listener
		IntentFilter filter = new IntentFilter();
		filter.addAction(NuxeoBroadcastMessages.NUXEO_SETTINGS_CHANGED);
		filter.addAction(NuxeoBroadcastMessages.NUXEO_SERVER_CONNECTIVITY_CHANGED);
		androidContext.registerReceiver(this, filter);
		initDefaultCaches();
	}

	protected void initDefaultCaches() {
		sqlStateManager = new SQLStateManager(androidContext);
		responseCacheManager = new DefaultResponseCacheManager(androidContext, sqlStateManager);
		deferredUpdateManager = new DefaultDeferedUpdateManager(sqlStateManager);
	}

	public NuxeoServerConfig getServerConfig() {
		return serverConfig;
	}

	public NuxeoNetworkStatus getNetworkStatus() {
		return networkStatus;
	}

	public synchronized Session getSession() {
		if (nuxeoSession==null) {
			if (responseCacheManager==null) {
				nuxeoClient = new HttpAutomationClient(
						serverConfig.getAutomationUrl());
			} else {
				nuxeoClient = new CacheAwareHttpAutomationClient(serverConfig.getAutomationUrl(), responseCacheManager, networkStatus, deferredUpdateManager);
			}
			nuxeoSession = nuxeoClient.getSession(
					serverConfig.getLogin(),
					serverConfig.getPassword());
		}
		return nuxeoSession;
	}

	public DocumentManager getDocumentManager() {
		return new DocumentManager(getSession());
	}

	public ResponseCacheManager getResponseCacheManager() {
		return responseCacheManager;
	}

	public void setResponseCacheManager(ResponseCacheManager cacheManager) {
		this.responseCacheManager = cacheManager;
	}

	public void setDeferredUpdateManager(
			DeferredUpdateManager deferredUpdateManager) {
		this.deferredUpdateManager = deferredUpdateManager;
	}

	public DeferredUpdateManager getDeferredUpdatetManager() {
		return deferredUpdateManager;
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

}
