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

package org.nuxeo.android.testsfrag;

import org.nuxeo.android.automation.NetworkSettingsActivity;
import org.nuxeo.android.automation.ServerSettingsActivity;
import org.nuxeo.android.automation.fragments.SettingsActivity;
import org.nuxeo.android.automationsample.R;
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
import android.widget.LinearLayout;

public class HomeSampleActivity extends Activity implements
        View.OnClickListener {

	protected final int SHOW_ACTIVITIES = 10010101;
    
    //fragments
    protected Button testFragBtn;
    
    protected Button connectFragButton;
    
    protected Button fetchDocFragBtn;
    
    protected Button simpleListFragBtn;
    
    protected Button browseFragBtn;
    
    protected Button docProviderFragBtn;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        testFragBtn = (Button)findViewById(R.id.nuxFragBtn);
        testFragBtn.setOnClickListener(this);
        
        connectFragButton = (Button)findViewById(R.id.connect_frag);
        connectFragButton.setOnClickListener(this);
        
        fetchDocFragBtn = (Button)findViewById(R.id.fetch_doc_frag);
        fetchDocFragBtn.setOnClickListener(this);

        simpleListFragBtn = (Button)findViewById(R.id.simple_list_frag);
        simpleListFragBtn.setOnClickListener(this);
        
        browseFragBtn = (Button)findViewById(R.id.browse_frag);
        browseFragBtn.setOnClickListener(this);
        
        docProviderFragBtn = (Button)findViewById(R.id.docProvider_frag_btn);
        docProviderFragBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
    	if (view == testFragBtn) {
            startActivity(new Intent(getApplicationContext(),
            		TestsFragActivity.class));
        } else if (view == simpleListFragBtn){
        	Intent simpleListFragIntent = new Intent(getApplicationContext(),
        			ListFragActivity.class);
        	simpleListFragIntent.putExtra("list", ListFragActivity.SIMPLE_LIST);
        	startActivity(simpleListFragIntent);
        } else if (view == browseFragBtn) {
        	Intent browseFragIntent = new Intent(getApplicationContext(),
        			ListFragActivity.class);
        	browseFragIntent.putExtra("list", ListFragActivity.BROWSE_LIST);
        	startActivity(browseFragIntent);
        } else if (view == docProviderFragBtn) {
        	Intent docProviderIntent = new Intent(getApplicationContext(),
        			ListFragActivity.class);
        	docProviderIntent.putExtra("list", ListFragActivity.DOCUMENT_PROVIDER);
        	startActivity(docProviderIntent);
        } else {
            Intent intent = new Intent(getApplicationContext(), BasicFragActivity.class);
            if (view == connectFragButton) {
            	intent.putExtra("frag", BasicFragActivity.CONNECT);
            } else if (view == fetchDocFragBtn) {
            	intent.putExtra("frag", BasicFragActivity.FETCH_DOCUMENT);
            }
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.simplemenu, menu);
        menu.add(Menu.NONE, SHOW_ACTIVITIES, 99, "Show activities");
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
        	startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        	break;
        case SHOW_ACTIVITIES:
        	startActivity(new Intent(getApplicationContext(), org.nuxeo.android.automationsample.HomeSampleActivity.class));
        	break;
        }
        return super.onOptionsItemSelected(item);
    }

    // to make testing easier
    protected NuxeoContext getNuxeoContext() {
        return NuxeoContext.get(getApplicationContext());
    }

    public void setSettings(String url, String login, String password) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); // getPreferences(MODE_PRIVATE);
        Editor prefEditor = prefs.edit();
        prefEditor.putString(NuxeoServerConfig.PREF_SERVER_URL, url);
        prefEditor.putString(NuxeoServerConfig.PREF_SERVER_LOGIN, login);
        prefEditor.putString(NuxeoServerConfig.PREF_SERVER_PASSWORD, password);
        prefEditor.commit();
    }

    public void resetAllCaches() {
        getNuxeoContext().getNuxeoClient().getDeferredUpdatetManager().purgePendingUpdates();
        getNuxeoContext().getNuxeoClient().getResponseCacheManager().clear();
        getNuxeoContext().getNuxeoClient().getFileUploader().purgePendingUploads();
    }

    public void fushPending() {
        getNuxeoContext().getNuxeoClient().getDeferredUpdatetManager().executePendingRequests(
                getNuxeoContext().getSession());
    }
}