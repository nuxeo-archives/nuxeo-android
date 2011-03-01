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

import org.json.JSONException;
import org.nuxeo.android.simpleclient.forms.LinearFormManager;
import org.nuxeo.android.simpleclient.service.NuxeoAndroidServices;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smartnsoft.droid4me.app.AppPublics.BroadcastListenerProvider;
import com.smartnsoft.droid4me.app.AppPublics.SendLoadingIntent;
import com.smartnsoft.droid4me.download.ImageDownloader;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy;

public final class DocumentViewActivity extends BaseDocumentViewActivity
        implements BusinessObjectsRetrievalAsynchronousPolicy,
        SendLoadingIntent, BroadcastListenerProvider,
        NuxeoAndroidApplication.TitleBarShowHomeFeature,
        NuxeoAndroidApplication.TitleBarRefreshFeature {

    private TextView title;
    private RelativeLayout layout;
    private LinearLayout linearLayout;
    private ImageButton pdfAction;
    private ImageButton downloadAction;

    @Override
    public void onRetrieveDisplayObjects() {

        setContentView(R.layout.document_view_layout);
        layout = (RelativeLayout) findViewById(R.id.documentLayout);
        linearLayout = (LinearLayout) findViewById(R.id.linearDocumentLayout);
        title = (TextView) findViewById(R.id.title);
        pdfAction = (ImageButton) findViewById(R.id.pdfBtn);
        downloadAction = (ImageButton) findViewById(R.id.downloadBtn);
        icon = (ImageView) findViewById(R.id.icon);
    }

    @Override
    public void onRetrieveBusinessObjects()
            throws BusinessObjectUnavailableException {
        fetchDocument(false);
        fetchIcon(document);
    }

    @Override
    public void onFulfillDisplayObjects() {

        if (document != null) {
            title.setText(document.getTitle());
            displayMetaData(linearLayout, document);
            pdfAction.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(DocumentViewActivity.this,
                            "started PDF Conversion",
                            Toast.LENGTH_SHORT).show();
                    downloadAndDisplayBlob("pdf");
                }
            });
            downloadAction.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(DocumentViewActivity.this,
                            "started download",
                            Toast.LENGTH_SHORT).show();
                    downloadAndDisplayBlob("file");
                }
            });

        }

    }

    protected void displayMetaData(LinearLayout currentLayout,  Document currentDocument) {
        try {
            LinearFormManager.displayForm(this, currentLayout, currentDocument);
        } catch (JSONException e) {
            log.error("Error while generatic display form", e);
        }
    }

    @Override
    protected String getSchemas() {
        return NuxeoAndroidServices.DEFAULT_SCHEMAS;
    }

    @Override
    protected Blob executeDownloadOperation(String flag) throws BusinessObjectUnavailableException {
        if ("file".equals(flag)) {
            return NuxeoAndroidServices.getInstance().getBlob(document.getId(), "file:content", refresh, false);
        } else {
            return NuxeoAndroidServices.getInstance().getPDF(document.getId(), refresh, true);
        }
    }

    @Override
    protected String getDownloadedFilePath(Blob blob, String flag) {
        if ("file".equals(flag)) {
            return "/sdcard/NuxeoAndroid/nuxeo-doc";
        } else {
            return "/sdcard/NuxeoAndroid/nuxeo-pdf-view.pdf";
        }
    }

    @Override
    protected String getDownloadedMimeType(Blob blob, String flag) {
        if ("file".equals(flag)) {
            return super.getDownloadedMimeType(blob, flag);
        }
        return "application/pdf";
    }

}
