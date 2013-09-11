package org.nuxeo.android.automation.fragments;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.android.automation.R;
import org.nuxeo.android.config.NuxeoServerConfig;
import org.nuxeo.android.fragments.AbstractNuxeoSettingsFragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ServerSettingsFragment extends AbstractNuxeoSettingsFragment implements OnClickListener {


    protected TextView login;

    protected TextView password;

    protected TextView serverUrl;

    protected Button saveButton;

    CharSequence initPasswordStr, initLoginStr, initUrlStr;

	public ServerSettingsFragment() {
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	View v = inflater.inflate(R.layout.settings, container, false);

        login = (TextView) v.findViewById(R.id.editLogin);
        initLoginStr = login.getText();
        password = (TextView) v.findViewById(R.id.editPassword);
        initPasswordStr = password.getText();
        serverUrl = (TextView) v.findViewById(R.id.editServerUrl);
        initUrlStr = serverUrl.getText();
        saveButton = (Button) v.findViewById(R.id.saveSettingsButton);
        saveButton.setOnClickListener(this);
        refreshDisplay();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshDisplay();
    }

    protected void refreshDisplay() {
        Map<String, Object> prefs = getNuxeoPreferences();
        serverUrl.setText(prefs.get(NuxeoServerConfig.PREF_SERVER_URL).toString());
        login.setText(prefs.get(NuxeoServerConfig.PREF_SERVER_LOGIN).toString());
        password.setText(prefs.get(NuxeoServerConfig.PREF_SERVER_PASSWORD).toString());
    }

    @Override
    public void onClick(View view) {
        if (view == saveButton) {
            Map<String, Object> prefs = new HashMap<String, Object>();

            if (!initUrlStr.equals(serverUrl.getText()) || !initLoginStr.equals(login.getText())
            		|| !initPasswordStr.equals(password.getText()))
            {
	            prefs.put(NuxeoServerConfig.PREF_SERVER_URL,
	                    serverUrl.getText().toString());
	            prefs.put(NuxeoServerConfig.PREF_SERVER_LOGIN,
	                    login.getText().toString());
	            prefs.put(NuxeoServerConfig.PREF_SERVER_PASSWORD,
	                    password.getText().toString());
	            saveNuxeoPreferences(prefs);
	            getActivity().setResult(Activity.RESULT_OK);
            }

            getActivity().finish();
        }
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return false;
    }

}
