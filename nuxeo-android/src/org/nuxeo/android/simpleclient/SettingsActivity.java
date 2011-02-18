/*
 * (C) Copyright 2010-2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Julien Carsique, Jocelyn Girard
 *
 */

package org.nuxeo.android.simpleclient;

import java.net.MalformedURLException;
import java.net.URL;

import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;

import com.smartnsoft.droid4me.app.SmartPreferenceActivity;

/**
 * The activity which enables to tune the application.
 *
 * @author Nuxeo & Smart&Soft
 * @since 2011.02.17
 */
public final class SettingsActivity extends SmartPreferenceActivity<Void> implements
        OnPreferenceChangeListener {

    public static final String PREF_SERVER_NAME = "server_name";

    public static final String PREF_SERVER_URL = "server_url";

    public static final String PREF_SERVER_URL_SUFFIX = "/site/automation/";

    public static final String PREF_LOGIN = "server_login";

    public static final String PREF_PASSWORD = "server_password";

    private EditTextPreference serverName;

    private EditTextPreference serverURL;

    private EditTextPreference login;

    // private EditTextPreference password;

    public void onRetrieveDisplayObjects() {
        addPreferencesFromResource(R.xml.settings);
        {

            final Preference versionPreference = findPreference("version");
            try {
                versionPreference.setSummary(getPackageManager().getPackageInfo(
                        getPackageName(), 0).versionName);
            } catch (NameNotFoundException exception) {
                if (log.isErrorEnabled()) {
                    log.error("Cannot determine the application version name",
                            exception);
                }
                versionPreference.setSummary("???");
            }
        }

        serverName = (EditTextPreference) findPreference(PREF_SERVER_NAME);
        serverURL = (EditTextPreference) findPreference(PREF_SERVER_URL);
        login = (EditTextPreference) findPreference(PREF_LOGIN);
        // password = (EditTextPreference) findPreference(PREF_PASSWORD);
    }

    @Override
    public void onFulfillDisplayObjects() {
        super.onFulfillDisplayObjects();
        setPreferenceSummary(serverName);
        serverName.setOnPreferenceChangeListener(this);
        setPreferenceSummary(serverURL);
        serverURL.setOnPreferenceChangeListener(this);
        setPreferenceSummary(login);
        login.setOnPreferenceChangeListener(this);
    }

    private void setPreferenceSummary(EditTextPreference pref) {
        setPreferenceSummary(pref, null);
    }

    public void setPreferenceSummary(EditTextPreference editTextPreference,
            String newValue) {
        if (editTextPreference.getText().length() > 0) {
            String value = (newValue != null ? newValue
                    : editTextPreference.getText());
            editTextPreference.setSummary(value);
        }
    }

    public boolean onPreferenceChange(Preference preference,
            Object newValueObject) {
        if (preference instanceof EditTextPreference) {
            String newValue = (String) newValueObject;
            if (newValue == null || newValue.length() == 0) {
                return false;
            }
            if (PREF_SERVER_URL.equals(preference.getKey())) {
                try {
                    new URL(newValue);
                } catch (MalformedURLException e) {
                    return false;
                }
            }

            setPreferenceSummary((EditTextPreference) preference, newValue);
        }
        return true;
    }
}
