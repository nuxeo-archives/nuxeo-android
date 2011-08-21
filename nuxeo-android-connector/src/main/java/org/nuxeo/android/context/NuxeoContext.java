package org.nuxeo.android.context;

import org.nuxeo.android.config.NuxeoOfflineSettings;
import org.nuxeo.android.config.NuxeoServerConfig;
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

	protected NuxeoServerConfig serverConfig = new NuxeoServerConfig();

	protected NuxeoOfflineSettings offlineSettings = new NuxeoOfflineSettings();

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

	public NuxeoServerConfig getServerConfig() {
		return serverConfig;
	}

	public void setServerConfig(NuxeoServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}

	public NuxeoOfflineSettings getOfflineSettings() {
		return offlineSettings;
	}

	public void setOfflineSettings(NuxeoOfflineSettings offlineSettings) {
		this.offlineSettings = offlineSettings;
	}

	public synchronized Session getSession() {
		if (nuxeoSession==null) {
			if (cacheManager==null) {
				nuxeoClient = new HttpAutomationClient(
						serverConfig.getAutomationUrl());
			} else {
				nuxeoClient = new CacheAwareHttpAutomationClient(serverConfig.getAutomationUrl(), cacheManager);
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
	}

	public NetworkStatusBroadCastReceiver getNetworkStatusBroadCastReceiver() {
		if (networkStatusBroadCastReceiver==null) {
			networkStatusBroadCastReceiver = new NetworkStatusBroadCastReceiver(getServerConfig(), getOfflineSettings());
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
