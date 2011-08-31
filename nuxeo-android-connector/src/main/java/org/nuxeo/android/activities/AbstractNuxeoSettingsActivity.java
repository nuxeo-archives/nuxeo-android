package org.nuxeo.android.activities;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.android.config.NuxeoServerConfig;

import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class AbstractNuxeoSettingsActivity extends BaseNuxeoActivity {

	protected boolean saveNuxeoPreferences(Map<String, Object> prefData) {

		SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); //getPreferences(MODE_PRIVATE);

		Editor prefEditor = prefs.edit();

		for (String key : prefData.keySet()) {

			Object value = prefData.get(key);

			if (value instanceof String) {
				if (!prefs.contains(key) || !((String)value).equals(prefs.getString(key, null))) {
					prefEditor.putString(key, (String) value);
				}
			}
			else if (value instanceof Boolean) {
				if (!((Boolean)value).equals(prefs.getBoolean(key, false))) {
					prefEditor.putBoolean(key, (Boolean) value);
				}
			}
			else if (value instanceof Long) {
				if (!prefs.contains(key) || !((Long)value).equals(prefs.getLong(key, 0))) {
					prefEditor.putLong(key, (Long) value);
				}
			}
			else if (value instanceof Float) {
				if (!prefs.contains(key) || !((Float)value).equals(prefs.getFloat(key, 0))) {
					prefEditor.putFloat(key, (Float) value);
				}
			}
			else if (value instanceof Integer) {
				if (!prefs.contains(key) || !((Integer)value).equals(prefs.getInt(key, 0))) {
					prefEditor.putInt(key, (Integer) value);
				}
			}
		}
		boolean commited = prefEditor.commit();

		return commited;
	}

	protected Map<String, Object> getNuxeoPreferences() {

		Map<String, Object> nxPrefs = new HashMap<String, Object>();

		nxPrefs.put(NuxeoServerConfig.PREF_SERVER_URL, getNuxeoContext().getServerConfig().getServerBaseUrl());
		nxPrefs.put(NuxeoServerConfig.PREF_SERVER_LOGIN, getNuxeoContext().getServerConfig().getLogin());
		nxPrefs.put(NuxeoServerConfig.PREF_SERVER_PASSWORD, getNuxeoContext().getServerConfig().getPassword());


		// XXX cache settings here

		return nxPrefs;

	}

}
