package org.nuxeo.android.context;

import org.nuxeo.android.config.NuxeoOfflineSettings;
import org.nuxeo.android.config.NuxeoServerConfig;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;

import android.content.Context;

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

	protected Session nuxeoSession;

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
			nuxeoClient = new HttpAutomationClient(
					serverConfig.getAutomationUrl());
			nuxeoSession = nuxeoClient.getSession(
					serverConfig.getLogin(),
					serverConfig.getPassword());
		}
		return nuxeoSession;
	}

	public DocumentManager getDocumentManager() {
		return new DocumentManager(getSession());
	}

}
