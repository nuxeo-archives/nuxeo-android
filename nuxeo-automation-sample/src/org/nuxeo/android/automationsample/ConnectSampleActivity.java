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

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.android.activities.BaseNuxeoActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ConnectSampleActivity extends BaseNuxeoActivity {

    protected TextView statusText;

    protected ListView listView;

    protected List<String> opList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect);

        statusText = (TextView) findViewById(R.id.statusText);
        statusText.setText("Connecting ...");
        listView = (ListView)findViewById(R.id.listView);
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        opList = new ArrayList<String>();
        opList.addAll(getNuxeoSession().getOperations().keySet());
        return true; // warn : returning null will disable the callba	ck !!!
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, opList);
    	listView.setAdapter(adapter);
      statusText.setText("Connected : " + opList.size()
      + "operations available");
    }

}
