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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.nuxeo.android.activities.BaseNuxeoActivity;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class HistorySampleActivity extends BaseNuxeoActivity {

    protected TextView title = null;

    protected TextView auditList = null;

    protected List<JSONObject> auditEntries = null;

    public static final String DOCUMENT = "document";

    protected Document currentDocument;

    protected static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd/MM/yy hh:mm:ss");

    protected Document getCurrentDocument() {
        if (currentDocument == null) {
            currentDocument = (Document) getIntent().getExtras().get(DOCUMENT);
        }
        return currentDocument;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        title = (TextView) findViewById(R.id.currentDocTitle);
        title.setText("History for " + getCurrentDocument().getTitle());
        auditList = (TextView) findViewById(R.id.historyContent);
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {

        auditEntries = (List<JSONObject>) data;

        StringBuffer sb = new StringBuffer();
        try {
            for (JSONObject entry : auditEntries) {
                sb.append(entry.getString("eventId").replace("document", ""));
                String eventDate = entry.getString("eventDate");
                if (eventDate != null) {
                    sb.append(" - ");
                    Date date = new Date(Long.parseLong(eventDate));
                    sb.append(dateFormat.format(date));
                }
                sb.append(" - ");
                sb.append(entry.getString("principal"));
                sb.append("\n");
            }
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(),
                    "Unable to read the audit entries", e);
        }
        auditList.setText(sb.toString());
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        return getNuxeoSession().getAdapter(DocumentManager.class).getAuditEntriesForDocument(
                getCurrentDocument().getId(), false);
    }

}
