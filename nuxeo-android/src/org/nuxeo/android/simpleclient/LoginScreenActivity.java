package org.nuxeo.android.simpleclient;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.smartnsoft.droid4me.app.SmartActivity;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;

public class LoginScreenActivity extends SmartActivity implements
        OnClickListener {

    private EditText editPassword;

    private EditText editLogin;

    private EditText editServerUrl;

    private Button buttonLogin;

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
                SettingsActivity.PREF_LOGIN, ""));
        editPassword.setText(getPreferences().getString(
                SettingsActivity.PREF_PASSWORD, ""));
        editServerUrl.setText(getPreferences().getString(
                SettingsActivity.PREF_SERVER_URL, ""));
        buttonLogin.setOnClickListener(this);
    }

    @Override
    public void onSynchronizeDisplayObjects() {
        // TODO Auto-generated method stub

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
            editor.putBoolean("firstLogin", true);
            editor.commit();

            startActivity(new Intent(this, HomeActivity.class));
            break;

        default:
            break;
        }
    }

}
