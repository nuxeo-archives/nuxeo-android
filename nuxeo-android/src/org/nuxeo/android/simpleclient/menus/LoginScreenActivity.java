/*
 * (C) Copyright 2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     Nuxeo - initial API and implementation
 */
package org.nuxeo.android.simpleclient.menus;

import org.nuxeo.android.simpleclient.HomeActivity;
import org.nuxeo.android.simpleclient.R;
import org.nuxeo.android.simpleclient.ui.TitleBarAggregate;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.smartnsoft.droid4me.app.AppPublics;
import com.smartnsoft.droid4me.app.AppPublics.BroadcastListener;
import com.smartnsoft.droid4me.app.SmartActivity;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;

public class LoginScreenActivity extends SmartActivity<TitleBarAggregate>
        implements AppPublics.SendLoadingIntent,
        AppPublics.BroadcastListenerProvider, View.OnClickListener {

    private EditText editPassword;

    private EditText editLogin;

    private EditText editServerUrl;

    private Button buttonLogin;

    public BroadcastListener getBroadcastListener() {
        return new AppPublics.LoadingBroadcastListener(this, true) {
            @Override
            protected void onLoading(boolean isLoading) {
                getAggregate().getAttributes().toggleRefresh(isLoading);
            }
        };
    }

    @Override
    public void onRetrieveDisplayObjects() {
        setContentView(R.layout.login_screen);

        editServerUrl = (EditText) findViewById(R.id.editServerUrl);
        editLogin = (EditText) findViewById(R.id.editLogin);
        editPassword = (EditText) findViewById(R.id.editPassword);
        buttonLogin = (Button) findViewById(R.id.loginButton);
    }

    @Override
    public void onRetrieveBusinessObjects()
            throws BusinessObjectUnavailableException {

    }

    @Override
    public void onFulfillDisplayObjects() {
        editLogin.setText(getPreferences().getString(
                SettingsActivity.PREF_LOGIN, "droidUser"));
        editPassword.setText(getPreferences().getString(
                SettingsActivity.PREF_PASSWORD, "nuxeo4android"));
        editServerUrl.setText(getPreferences().getString(
                SettingsActivity.PREF_SERVER_URL,
                "http://android.demo.nuxeo.com/nuxeo"));
        buttonLogin.setOnClickListener(this);
    }

    @Override
    public void onSynchronizeDisplayObjects() {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.loginButton:
            final Editor editor = getPreferences().edit();

            editor.putString(SettingsActivity.PREF_LOGIN,
                    editLogin.getText().toString());
            editor.putString(SettingsActivity.PREF_PASSWORD,
                    editPassword.getText().toString());
            editor.putString(SettingsActivity.PREF_SERVER_URL,
                    editServerUrl.getText().toString());
            editor.commit();

            startActivity(new Intent(this, HomeActivity.class));
            finish();
            break;

        default:
            break;
        }
    }

}
