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
 *     Jocelyn Girard, Edouard Mercier, Thierry Delprat
 *
 */

package org.nuxeo.android.simpleclient;

import org.nuxeo.android.simpleclient.service.NuxeoAndroidServices;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smartnsoft.droid4me.app.AppPublics.BroadcastListenerProvider;
import com.smartnsoft.droid4me.app.AppPublics.SendLoadingIntent;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy;

public final class DocumentViewActivity extends BaseDocumentViewActivity
        implements BusinessObjectsRetrievalAsynchronousPolicy,
        SendLoadingIntent, BroadcastListenerProvider,
        NuxeoAndroidApplication.TitleBarShowHomeFeature,
        NuxeoAndroidApplication.TitleBarRefreshFeature {

    private TextView description;
    private TextView title;
    private LinearLayout layout;
    private Button pdfAction;

    @Override
    public void onRetrieveDisplayObjects() {

        setContentView(R.layout.document_view_layout);
        layout = (LinearLayout) findViewById(R.id.documentLayout);
        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        pdfAction = (Button) findViewById(R.id.pdfBtn);
    }

    @Override
    public void onRetrieveBusinessObjects()
            throws BusinessObjectUnavailableException {
        fetchDocument(false);
    }

    @Override
    public void onFulfillDisplayObjects() {

        if (document != null) {
            title.setText(document.getTitle());
            description.setText(document.getString("dc:description", ""));
            displayMetaData(layout, document);
            pdfAction.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(DocumentViewActivity.this,
                            "started PDF Conversion",
                            Toast.LENGTH_SHORT).show();
                    downloadAndDisplayBlob();
                }
            });
       }

    }

    protected void displayMetaData(LinearLayout currentLayout,  Document currentDocument) {
        for (String key : currentDocument.getProperties().getKeys()) {
            final TextView textView = new TextView(this);

            if (key != null) {
                try {
                    final String value = currentDocument.getString(key, "");
                    if (value != null) {
                        textView.setText(key + " => " + value);
                        final int padding = getResources().getDimensionPixelSize(
                                R.dimen.defaultPadding);
                        textView.setPadding(padding, padding, padding,
                                padding);
                        currentLayout.addView(textView);
                    }
                } catch (Exception exception) {
                    if (log.isWarnEnabled()) {
                        log.warn("NULL", exception);
                    }
                }
            }
        }
    }

    @Override
    protected String getSchemas() {
        return NuxeoAndroidServices.DEFAULT_SCHEMAS;
    }

    @Override
    protected Blob executeDownloadOperation() throws BusinessObjectUnavailableException {
        return NuxeoAndroidServices.getInstance().getPDF(document.getId(), refresh, true);
    }

    @Override
    protected String getDownloadedFilePath(Blob blob) {
        return "/sdcard/NuxeoAndroid/nuxeo-pdf-view.pdf";
    }

    @Override
    protected String getDownloadedMimeType(Blob blob) {
        return "application/pdf";
    }

}
