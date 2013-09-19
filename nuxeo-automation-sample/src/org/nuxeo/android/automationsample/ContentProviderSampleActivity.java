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

import org.nuxeo.android.activities.BaseListActivity;
import org.nuxeo.android.contentprovider.NuxeoContentProviderConfig;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.database.Cursor;
import android.net.Uri;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * @deprecated since 2.0
 * 
 */
@Deprecated
public class ContentProviderSampleActivity extends BaseListActivity {

    protected Cursor cursor;

    protected void setupViews() {
        setContentView(R.layout.nxcp);
        waitingMessage = (TextView) findViewById(R.id.waitingMessage);
        refreshBtn = findViewById(R.id.refreshBtn);
        listView = (ListView) findViewById(R.id.myList);
    }

    // Executed on the background thread to avoid freezing the UI
    @Override
    protected Object retrieveNuxeoData() throws Exception {
//        cursor = managedQuery(
//                Uri.parse("content://"
//                        + NuxeoContentProviderConfig.getAuthority()
//                        + "/documents"), null, null, null, null);
        cursor = getContentResolver().query(Uri.parse("content://"
                        + NuxeoContentProviderConfig.getAuthority()
                        + "/documents"), null, null, null, null);
        return true;
    }

    // Called on the UIThread when Nuxeo data has been retrieved
    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        super.onNuxeoDataRetrieved(data);
        final String[] columns = new String[] { "_ID", "dc:title", "status",
                "iconUri" };
        final int[] to = new int[] { R.id.id_entry, R.id.title_entry,
                R.id.status_entry, R.id.iconView };
        SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this,
                R.layout.list_item, cursor, columns, to);
        listView.setAdapter(mAdapter);
    }

    protected Document getContextMenuDocument(int selectedPosition) {
        // return documentCursor.getDocument(selectedPosition);
        return null;
    }

    protected void doRefresh() {
        // documentCursor.getDocumentsList().refreshAll();
    }

    @Override
    protected void onListItemClicked(int listItemPosition) {

    }

    @Override
    protected void populateMenu(Menu menu) {
    }

}
