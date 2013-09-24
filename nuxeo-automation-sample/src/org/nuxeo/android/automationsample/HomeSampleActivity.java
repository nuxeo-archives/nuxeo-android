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

package org.nuxeo.android.automationsample;

import org.nuxeo.android.automation.NetworkSettingsActivity;
import org.nuxeo.android.automation.ServerSettingsActivity;
import org.nuxeo.android.automation.fragments.SettingsActivity;
import org.nuxeo.android.config.NuxeoServerConfig;
import org.nuxeo.android.context.NuxeoContext;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Launches all the old style activities (without fragments). For new
 * activities, use {@link org.nuxeo.android.activities.HomeSampleActivity}
 * 
 * @see org.nuxeo.android.activities.HomeSampleActivity
 */
public class HomeSampleActivity extends Activity implements
		View.OnClickListener {

	protected final int SHOW_FRAGMENTS = 10101;

	protected Button connectBtn;

	protected Button fetchDocBtn;

	protected Button simpleListBtn;

	protected Button browseBtn;

	protected Button docProviderBtn;

	protected Button contentProviderBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_act);

		connectBtn = (Button) findViewById(R.id.connect);
		connectBtn.setOnClickListener(this);

		fetchDocBtn = (Button) findViewById(R.id.fetch_doc);
		fetchDocBtn.setOnClickListener(this);

		simpleListBtn = (Button) findViewById(R.id.simple_list);
		simpleListBtn.setOnClickListener(this);

		browseBtn = (Button) findViewById(R.id.browse);
		browseBtn.setOnClickListener(this);

		docProviderBtn = (Button) findViewById(R.id.docProviderBtn);
		docProviderBtn.setOnClickListener(this);

		contentProviderBtn = (Button) findViewById(R.id.contentProviderBtn);
		contentProviderBtn.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		if (view == connectBtn) {
			startActivity(new Intent(getApplicationContext(),
					ConnectSampleActivity.class));
		} else if (view == fetchDocBtn) {
			startActivity(new Intent(getApplicationContext(),
					SimpleFetchSampleActivty.class));
		} else if (view == simpleListBtn) {
			startActivity(new Intent(getApplicationContext(),
					SimpleDocumentsListSampleActivity.class));
		} else if (view == browseBtn) {
			startActivity(new Intent(getApplicationContext(),
					GetChildrenSampleActivity.class));
		} else if (view == docProviderBtn) {
			startActivity(new Intent(getApplicationContext(),
					DocumentProviderSampleActivity.class));
		} else if (view == contentProviderBtn) {
			// startActivity(new Intent(getApplicationContext(),
			// ContentProviderSampleActivity.class));
			Toast.makeText(this, "not implemented", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.simplemenu, menu);
		menu.add(Menu.NONE, SHOW_FRAGMENTS, 99, "Show fragments");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle all of the possible menu actions.
		switch (item.getItemId()) {

		case R.id.itemNetworkConfig:
			startActivity(new Intent(getApplicationContext(),
					NetworkSettingsActivity.class));
			break;
		case R.id.itemServerSettings:
			startActivity(new Intent(getApplicationContext(),
					ServerSettingsActivity.class));
			break;
		case R.id.itemNetworkConfigFrag:
			startActivity(new Intent(getApplicationContext(),
					SettingsActivity.class));
			break;
		case SHOW_FRAGMENTS:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// to make testing easier
	protected NuxeoContext getNuxeoContext() {
		return NuxeoContext.get(getApplicationContext());
	}

	public void setSettings(String url, String login, String password) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext()); // getPreferences(MODE_PRIVATE);
		Editor prefEditor = prefs.edit();
		prefEditor.putString(NuxeoServerConfig.PREF_SERVER_URL, url);
		prefEditor.putString(NuxeoServerConfig.PREF_SERVER_LOGIN, login);
		prefEditor.putString(NuxeoServerConfig.PREF_SERVER_PASSWORD, password);
		prefEditor.commit();
	}

	public void resetAllCaches() {
		getNuxeoContext().getNuxeoClient().getDeferredUpdatetManager()
				.purgePendingUpdates();
		getNuxeoContext().getNuxeoClient().getResponseCacheManager().clear();
		getNuxeoContext().getNuxeoClient().getFileUploader()
				.purgePendingUploads();
	}

    public void setOffline(boolean offline) {
        getNuxeoContext().getNetworkStatus().setForceOffline(offline);
    }

	public void fushPending() {
		getNuxeoContext().getNuxeoClient().getDeferredUpdatetManager()
				.executePendingRequests(getNuxeoContext().getSession());
	}
}