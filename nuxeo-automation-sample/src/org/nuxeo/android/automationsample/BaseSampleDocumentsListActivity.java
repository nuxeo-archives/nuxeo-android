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

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.android.activities.BaseDocumentLayoutActivity;
import org.nuxeo.android.activities.BaseDocumentsListActivity;
import org.nuxeo.android.adapters.AbstractDocumentListAdapter;
import org.nuxeo.android.adapters.DocumentsListAdapter;
import org.nuxeo.android.documentprovider.LazyDocumentsList;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.widget.ListView;
import android.widget.TextView;

public abstract class BaseSampleDocumentsListActivity extends
        BaseDocumentsListActivity {

    protected Document userHome;

    public BaseSampleDocumentsListActivity() {
        super();
    }

    @Override
    protected Class<? extends BaseDocumentLayoutActivity> getEditActivityClass() {
        return DocumentLayoutActivity.class;
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        // fetch User's home
        userHome = getNuxeoContext().getDocumentManager().getUserHome();
        // let base class fetch the list
        return super.retrieveNuxeoData();
    }

    protected Map<Integer, String> getMapping() {
        Map<Integer, String> mapping = new HashMap<Integer, String>();
        mapping.put(R.id.title_entry, "dc:title");
        mapping.put(R.id.status_entry, "status");
        mapping.put(R.id.iconView, "iconUri");
        mapping.put(R.id.description, "dc:description");
        mapping.put(R.id.id_entry, "uuid");
        return mapping;
    }

    @Override
    protected void setupViews() {
        setContentView(R.layout.nxcp);
        waitingMessage = (TextView) findViewById(R.id.waitingMessage);
        refreshBtn = findViewById(R.id.refreshBtn);
        listView = (ListView) findViewById(R.id.myList);
    }

    @Override
    protected void displayDocumentList(ListView listView,
            LazyDocumentsList documentsList) {
        AbstractDocumentListAdapter adapter = new DocumentsListAdapter(this,
                documentsList, R.layout.list_item, getMapping(),
                R.layout.list_item_loading);
        listView.setAdapter(adapter);
    }

    @Override
    protected Document initNewDocument(String type) {
        return new Document(userHome.getPath(), "newAndroidDoc", type);
    }

}