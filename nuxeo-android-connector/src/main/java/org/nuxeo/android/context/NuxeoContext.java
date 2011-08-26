package org.nuxeo.android.context;

import org.nuxeo.android.config.NuxeoServerConfig;
import org.nuxeo.android.network.NetworkStatusBroadCastReceiver;
import org.nuxeo.android.network.NuxeoNetworkStatus;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.cache.CacheAwareHttpAutomationClient;
import org.nuxeo.ecm.automation.client.cache.InputStreamCacheManager;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 *
 * @author tiry
 *
 */
public class NuxeoContext {

	protected static NuxeoContext instance = null;

	protected NuxeoServerConfig serverConfig;

	protected NuxeoNetworkStatus networkStatus;

	protected HttpAutomationClient nuxeoClient;

	protected InputStreamCacheManager cacheManager;

	protected Session nuxeoSession;

	protected ConnectivityManager connectivityManager;

	protected NetworkStatusBroadCastReceiver networkStatusBroadCastReceiver;

	public static NuxeoContext get(Context context) {
		if (context instanceof NuxeoContextProvider) {
			NuxeoContextProvider nxApp = (NuxeoContextProvider) context;
			return nxApp.getNuxeoContext();
		} else {
			if (instance==null) {
				//instance = new NuxeoContext();
			}
			return instance;
		}
	}

	public NuxeoContext() {
		serverConfig = new NuxeoServerConfig();
	}

	public NuxeoServerConfig getServerConfig() {
		return serverConfig;
	}

	public NuxeoNetworkStatus getNetworkStatus() {
		if (networkStatus==null) {
			networkStatus = new NuxeoNetworkStatus(serverConfig, connectivityManager);
		}
		return networkStatus;
	}

	public synchronized Session getSession() {
		if (nuxeoSession==null) {
			if (cacheManager==null) {
				nuxeoClient = new HttpAutomationClient(
						serverConfig.getAutomationUrl());
			} else {
				nuxeoClient = new CacheAwareHttpAutomationClient(serverConfig.getAutomationUrl(), cacheManager, networkStatus);
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

	public ConnectivityManager getConnectivityManager() {
		return connectivityManager;
	}

	public void setConnectivityManager(ConnectivityManager connectivityManager) {
		this.connectivityManager = connectivityManager;
		if (networkStatus==null) {
			networkStatus = new NuxeoNetworkStatus(serverConfig, connectivityManager);
		}
	}

	public NetworkStatusBroadCastReceiver getNetworkStatusBroadCastReceiver() {
		if (networkStatusBroadCastReceiver==null) {
			networkStatusBroadCastReceiver = new NetworkStatusBroadCastReceiver(getNetworkStatus());
		}
		return networkStatusBroadCastReceiver;
	}

	public InputStreamCacheManager getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(InputStreamCacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}



}
