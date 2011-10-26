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
package org.nuxeo.android.simpleclient.otherviews;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.nuxeo.android.simpleclient.R;
import org.nuxeo.android.simpleclient.service.NuxeoAndroidServices;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.widget.ImageView;
import android.widget.TextView;

import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;

public class HistoryActivity extends BaseMiscActivity {

    protected TextView title = null;

    protected TextView auditList = null;

    protected static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd/MM/yy hh:mm:ss");

    private List<JSONObject> auditEntries = null;

    protected List<JSONObject> getDocumentAuditEntries() {
        return auditEntries;
    }

    protected List<JSONObject> fetchDocumentAuditEntries(boolean forceRefresh)
            throws BusinessObjectUnavailableException {
        if (auditEntries == null || refresh || forceRefresh) {
            auditEntries = NuxeoAndroidServices.getInstance().getAuditEntries(
                    getTargetDocId(), refresh || forceRefresh);
        }
        return auditEntries;
    }

    @Override
    public void onSynchronizeDisplayObjects() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFulfillDisplayObjects() {

        if (getTargetDoc() != null) {
            title.setText(getTargetDoc().getTitle());
        }
        StringBuffer sb = new StringBuffer();
        try {
            sb.append(auditEntries.size() + "\n");
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
            log.error("Unable to read the audit entries");
        }
        auditList.setText("History:\n" + sb.toString());
    }

    @Override
    public void onRetrieveBusinessObjects()
            throws BusinessObjectUnavailableException {
        fetchDocumentAuditEntries(refresh);
        Document doc = getTargetDoc();
        fetchIcon(doc);
    }

    @Override
    public void onRetrieveDisplayObjects() {
        setContentView(R.layout.history_view_layout);
        title = (TextView) findViewById(R.id.title);
        icon = (ImageView) findViewById(R.id.icon);
        auditList = (TextView) findViewById(R.id.auditList);
    }

}
