package org.nuxeo.android.config;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

public class NuxeoServerConfig implements OnSharedPreferenceChangeListener{

	public static final String PREF_SERVER_URL = "nuxeo.serverUrl";
	public static final String PREF_SERVER_LOGIN = "nuxeo.login";
	public static final String PREF_SERVER_PASSWORD = "nuxeo.password";

	protected Context androidContext;

	protected SharedPreferences sharedPrefs;

	protected String serverBaseUrl = "http://10.0.2.2:8080/nuxeo/"; // http://android.demo.nuxeo.com/nuxeo/

	protected String login = "Administrator"; // "droidUser";

	protected String password = "Administrator"; // "nuxeo4android";

	public NuxeoServerConfig(Context androidContext) {
		this.androidContext=androidContext;
		setSharedPrefs(PreferenceManager.getDefaultSharedPreferences(androidContext));
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getServerBaseUrl() {
		return serverBaseUrl;
	}

	public void setServerBaseUrl(String serverBaseUrl) {
		if (!serverBaseUrl.endsWith("/")) {
			serverBaseUrl = serverBaseUrl + "/";
		}
		this.serverBaseUrl = serverBaseUrl;
	}

	public String getAutomationUrl() {
		return serverBaseUrl + "site/automation";
	}

	public String getHost() {
		String url = getServerBaseUrl();
		URI uri;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			return null;
		}
		return uri.getHost();
	}

	public int getHostIP() {
		return getIPasInt(getHost());
	}

	protected  int getIPasInt(String hostname) {
		if (hostname==null) {
			return -1;
		}
	    InetAddress inetAddress;
	    try {
	        inetAddress = InetAddress.getByName(hostname);
	    } catch (UnknownHostException e) {
	        return -1;
	    }
	    byte[] addrBytes;
	    int addr;
	    addrBytes = inetAddress.getAddress();
	    addr = ((addrBytes[3] & 0xff) << 24)
	            | ((addrBytes[2] & 0xff) << 16)
	            | ((addrBytes[1] & 0xff) << 8)
	            |  (addrBytes[0] & 0xff);
	    return addr;
	}

	public SharedPreferences getSharedPrefs() {
		return sharedPrefs;
	}

	protected void setSharedPrefs(SharedPreferences sharedPrefs) {
		this.sharedPrefs = sharedPrefs;
		sharedPrefs.registerOnSharedPreferenceChangeListener(this);
		initFromPrefs(sharedPrefs);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		if (PREF_SERVER_LOGIN.equals(key) || PREF_SERVER_PASSWORD.equals(key) || PREF_SERVER_URL.equals(key)) {
			initFromPrefs(prefs);
			androidContext.sendBroadcast(new Intent(NuxeoBroadcastMessages.NUXEO_SETTINGS_CHANGED));
		}
	}

	protected void initFromPrefs(SharedPreferences prefs) {
		serverBaseUrl = prefs.getString(PREF_SERVER_URL, serverBaseUrl);
		login = prefs.getString(PREF_SERVER_LOGIN, login);
		password = prefs.getString(PREF_SERVER_PASSWORD, password);

	}

}
