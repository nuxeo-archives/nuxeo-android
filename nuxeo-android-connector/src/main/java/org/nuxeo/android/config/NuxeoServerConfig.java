/*
 * (C) Copyright 2011-2013 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 */

package org.nuxeo.android.config;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;
import org.nuxeo.ecm.automation.client.android.SessionCache;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

public class NuxeoServerConfig implements OnSharedPreferenceChangeListener {

    public static final String PREF_SERVER_URL = "nuxeo.serverUrl";

    public static final String PREF_SERVER_LOGIN = "nuxeo.login";

    public static final String PREF_SERVER_PASSWORD = "nuxeo.password";

    public static final String PREF_SERVER_TOKEN = "nuxeo.auth.token";

    private static final String PREF_CACHEKEY = null;

    private static final String TAG = "NuxeoServerConfig";

    protected Context androidContext;

    protected SharedPreferences sharedPrefs;

    // will work on an emulator if a nuxeo server is running on your computer
    protected String serverBaseUrl = "http://10.0.2.2:8080/nuxeo/";

    protected String login = "Administrator";

    protected String password = "Administrator";

    protected String token = null;

    public NuxeoServerConfig(Context androidContext) {
        this.androidContext = androidContext;
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

    /**
     * @since 2.0
     */
    public void setServerBaseUrl(Uri serverBaseUrl) {
        this.serverBaseUrl = serverBaseUrl.toString();
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

    protected int getIPasInt(String hostname) {
        if (hostname == null) {
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
        addr = ((addrBytes[3] & 0xff) << 24) | ((addrBytes[2] & 0xff) << 16)
                | ((addrBytes[1] & 0xff) << 8) | (addrBytes[0] & 0xff);
        return addr;
    }

    public SharedPreferences getSharedPrefs() {
        return sharedPrefs;
    }

    public void setSharedPrefs(SharedPreferences sharedPrefs) {
        this.sharedPrefs = sharedPrefs;
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
        initFromPrefs();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (PREF_SERVER_LOGIN.equals(key) || PREF_SERVER_PASSWORD.equals(key)
                || PREF_SERVER_URL.equals(key) || PREF_SERVER_TOKEN.equals(key)) {
            initFromPrefs();
        }
    }

    protected void initFromPrefs() {
        serverBaseUrl = sharedPrefs.getString(PREF_SERVER_URL, serverBaseUrl);
        login = sharedPrefs.getString(PREF_SERVER_LOGIN, login);
        password = sharedPrefs.getString(PREF_SERVER_PASSWORD, password);
        token = sharedPrefs.getString(PREF_SERVER_TOKEN, token);
        Log.d(TAG, "init url=" + serverBaseUrl);
        androidContext.sendBroadcast(new Intent(
                NuxeoBroadcastMessages.NUXEO_SETTINGS_CHANGED));
    }

    /**
     * @param authentication token
     * @since 2.0
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Default is the password. Change
     *
     * @return a key used for the {@link SessionCache}
     * @since 2.0
     */
    public String getCacheKey() {
        String key = sharedPrefs.getString(PREF_CACHEKEY, PREF_SERVER_PASSWORD);
        return sharedPrefs.getString(key, password);
    }

    /**
     * Set the {@link SharedPreferences} key to use for the {@link SessionCache}
     * . Default is {@link #PREF_SERVER_PASSWORD}. Change it if you want to use
     * the same session cache for a user with various authentication methods.
     *
     * @since 2.0
     * @param key This is not the cacheKey value used by the cache itself, but
     *            the preference key to use for retrieving the value. Default is
     *            the {@link #PREF_SERVER_PASSWORD}.
     */
    public void setCacheKey(String sharedPrefsKey) {
        Editor edit = sharedPrefs.edit();
        edit.putString(PREF_CACHEKEY, sharedPrefsKey);
        edit.commit();
    }

}
