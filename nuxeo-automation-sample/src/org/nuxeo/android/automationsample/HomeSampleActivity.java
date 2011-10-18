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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class HomeSampleActivity extends Activity implements View.OnClickListener{

	protected Button connectBtn;
	protected Button contentUriBtn;
	protected Button docListBtn;
	protected Button docProviderBtn;
	protected Button contentProviderBtn;
	protected Button browseBtn;

	protected Spinner spinner;

	protected List<String> opList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        connectBtn = (Button) findViewById(R.id.connect);
        connectBtn.setOnClickListener(this);

        contentUriBtn = (Button) findViewById(R.id.contentUriBtn);
        contentUriBtn.setOnClickListener(this);

        docListBtn = (Button) findViewById(R.id.docListBtn);
        docListBtn.setOnClickListener(this);

        browseBtn = (Button) findViewById(R.id.browsetBtn);
        browseBtn.setOnClickListener(this);

        docProviderBtn = (Button) findViewById(R.id.docProviderBtn);
        docProviderBtn.setOnClickListener(this);

        contentProviderBtn = (Button) findViewById(R.id.contentProviderBtn);
        contentProviderBtn.setOnClickListener(this);

        spinner = (Spinner) findViewById(R.id.opList);
        spinner.setVisibility(4);
    }


	@Override
	public void onClick(View view) {
		if (view == connectBtn) {
          startActivity(new Intent(getApplicationContext(),
                    ConnectSampleActivity.class));
		}
		else if (view == contentUriBtn) {
            startActivity(new Intent(getApplicationContext(),
                    SimpleFetchSampleActivty.class));
		}
		else if (view == contentProviderBtn) {
            startActivity(new Intent(getApplicationContext(),
                    ContentProviderSampleActivity.class));
		}
		else if (view == docListBtn) {
            startActivity(new Intent(getApplicationContext(),
                    SimpleDocumentsListSampleActivity.class));
		}
		else if (view == browseBtn) {
            startActivity(new Intent(getApplicationContext(),
                    GetChildrenSampleActivity.class));
		}
		else if (view == docProviderBtn) {
            startActivity(new Intent(getApplicationContext(),
                    DocumentProviderSampleActivity.class));
		}
		else {
			// start activity
			// See openIntend
			startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), 0);
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
	        }
	        return super.onOptionsItemSelected(item);
	 }


}