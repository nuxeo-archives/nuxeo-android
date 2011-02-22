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

import org.nuxeo.android.simpleclient.service.NuxeoAndroidServices;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.Preference;

import com.smartnsoft.droid4me.app.SmartPreferenceActivity;
import com.smartnsoft.droid4me.app.SmartSplashScreenActivity;

/**
 * The activity which enables to tune the application.
 *
 * @author Nuxeo & Smart&Soft
 * @since 2011.02.17
 */
public final class SettingsActivity extends
        SmartPreferenceActivity<NuxeoAndroidApplication.TitleBarAggregate>
        implements NuxeoAndroidApplication.TitleBarShowHomeFeature {

    public static final String PREF_SERVER_URL = "server_url";

    public static final String PREF_SERVER_URL_SUFFIX = "/site/automation/";

    public static final String PREF_LOGIN = "server_login";

    public static final String PREF_PASSWORD = "server_password";

    private static final String SIGN_OUT = "signOut";

    private Preference serverURL;

    private Preference login;

    private Preference signOut;

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

        serverURL = findPreference(PREF_SERVER_URL);
        login = findPreference(PREF_LOGIN);
        signOut = findPreference(SIGN_OUT);
    }

    @Override
    public void onFulfillDisplayObjects() {
        super.onFulfillDisplayObjects();
        serverURL.setSummary(getPreferences().getString(PREF_SERVER_URL, null));
        login.setSummary(getPreferences().getString(PREF_LOGIN, null));
        signOut.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getPreferences().edit().remove(PREF_PASSWORD).commit();
                NuxeoAndroidServices.getInstance().release();
                SmartSplashScreenActivity.markAsUnitialized(NuxeoAndroidSplashScreenActivity.class);
                startActivity(new Intent(getApplicationContext(),
                        HomeActivity.class));
                finish();
                return true;
            }
        });
    }

}
