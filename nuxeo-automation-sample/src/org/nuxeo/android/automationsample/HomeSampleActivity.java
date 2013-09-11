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

import java.util.List;

import org.nuxeo.android.automation.NetworkSettingsActivity;
import org.nuxeo.android.automation.ServerSettingsActivity;
import org.nuxeo.android.automation.fragments.SettingsActivity;
import org.nuxeo.android.config.NuxeoServerConfig;
import org.nuxeo.android.context.NuxeoContext;
import org.nuxeo.android.testsfrag.BasicFragActivity;
import org.nuxeo.android.testsfrag.ListFragActivity;
import org.nuxeo.android.testsfrag.TestsFragActivity;

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
import android.widget.Toast;

public class HomeSampleActivity extends Activity implements
        View.OnClickListener {

    protected LinearLayout activitiesLayout;
    
    
    protected Button showActivitesButton;
    protected boolean activitiesShown = false;
    //Activities
    protected Button connectBtn;

    protected Button fetchDocBtn;

    protected Button simpleListBtn;

    protected Button browseBtn;
    
    protected Button docProviderBtn;

    protected Button contentProviderBtn;

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
        
        activitiesLayout = (LinearLayout)findViewById(R.id.activities_layout);
        showActivitesButton = (Button)findViewById(R.id.activites_button);
        showActivitesButton.setOnClickListener(this);
        
        
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
    	if (view == showActivitesButton) {
    		if (!activitiesShown) {
    			activitiesLayout.setVisibility(View.VISIBLE);
    			activitiesShown = true;
    		} else {
    			activitiesLayout.setVisibility(View.GONE);
    			activitiesShown = false;
    		}
    	} else if (view == connectBtn) {
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
//          startActivity(new Intent(getApplicationContext(),
//                  ContentProviderSampleActivity.class));
      	Toast.makeText(this, "not implemented", Toast.LENGTH_LONG).show();
      	
      	//Fragment activities
        } else if (view == testFragBtn) {
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