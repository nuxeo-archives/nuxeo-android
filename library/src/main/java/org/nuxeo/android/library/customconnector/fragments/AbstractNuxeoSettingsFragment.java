package org.nuxeo.android.fragments;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import org.nuxeo.android.config.NuxeoServerConfig;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractNuxeoSettingsFragment extends BaseNuxeoFragment {

	public AbstractNuxeoSettingsFragment() {
	}


    protected boolean saveNuxeoPreferences(Map<String, Object> prefData) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()); // getPreferences(MODE_PRIVATE);

        Editor prefEditor = prefs.edit();

        for (String key : prefData.keySet()) {

            Object value = prefData.get(key);

            if (value instanceof String) {
                if (!prefs.contains(key)
                        || !((String) value).equals(prefs.getString(key, null))) {
                    prefEditor.putString(key, (String) value);
                }
            } else if (value instanceof Boolean) {
                if (!((Boolean) value).equals(prefs.getBoolean(key, false))) {
                    prefEditor.putBoolean(key, (Boolean) value);
                }
            } else if (value instanceof Long) {
                if (!prefs.contains(key)
                        || !((Long) value).equals(prefs.getLong(key, 0))) {
                    prefEditor.putLong(key, (Long) value);
                }
            } else if (value instanceof Float) {
                if (!prefs.contains(key)
                        || !((Float) value).equals(prefs.getFloat(key, 0))) {
                    prefEditor.putFloat(key, (Float) value);
                }
            } else if (value instanceof Integer) {
                if (!prefs.contains(key)
                        || !((Integer) value).equals(prefs.getInt(key, 0))) {
                    prefEditor.putInt(key, (Integer) value);
                }
            }
        }
        boolean committed = prefEditor.commit();

        return committed;
    }

    protected Map<String, Object> getNuxeoPreferences() {

        Map<String, Object> nxPrefs = new HashMap<String, Object>();

        NuxeoServerConfig serverConfig = getNuxeoContext().getServerConfig();
        nxPrefs.put(NuxeoServerConfig.PREF_SERVER_URL,
                serverConfig.getServerBaseUrl());
        nxPrefs.put(NuxeoServerConfig.PREF_SERVER_LOGIN,
                serverConfig.getLogin());
        nxPrefs.put(NuxeoServerConfig.PREF_SERVER_PASSWORD,
                serverConfig.getPassword());

        // XXX cache settings here

        return nxPrefs;
    }

}
