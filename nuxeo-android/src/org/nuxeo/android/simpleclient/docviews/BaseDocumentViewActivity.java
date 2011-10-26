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
package org.nuxeo.android.simpleclient.docviews;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.nuxeo.android.simpleclient.NuxeoAndroidApplication;
import org.nuxeo.android.simpleclient.menus.SettingsActivity;
import org.nuxeo.android.simpleclient.service.NuxeoAndroidServices;
import org.nuxeo.android.simpleclient.ui.TitleBarAggregate;
import org.nuxeo.android.simpleclient.ui.TitleBarRefreshFeature;
import org.nuxeo.android.simpleclient.ui.TitleBarShowHomeFeature;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.smartnsoft.droid4me.app.AppPublics;
import com.smartnsoft.droid4me.app.AppPublics.BroadcastListener;
import com.smartnsoft.droid4me.app.AppPublics.BroadcastListenerProvider;
import com.smartnsoft.droid4me.app.AppPublics.SendLoadingIntent;
import com.smartnsoft.droid4me.app.SmartActivity;
import com.smartnsoft.droid4me.download.ImageDownloader;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy;

public abstract class BaseDocumentViewActivity extends
        SmartActivity<TitleBarAggregate> implements
        BusinessObjectsRetrievalAsynchronousPolicy, SendLoadingIntent,
        BroadcastListenerProvider, TitleBarShowHomeFeature,
        TitleBarRefreshFeature {

    public static final String DOCUMENT_ID = "document_id";

    public static final String DOCUMENT = "document";

    protected boolean refresh = false;

    protected Document document = null;

    protected ImageView icon;

    public BroadcastListener getBroadcastListener() {
        return new AppPublics.LoadingBroadcastListener(this, true) {
            @Override
            protected void onLoading(boolean isLoading) {
                getAggregate().getAttributes().toggleRefresh(isLoading);
            }
        };
    }

    protected abstract String getSchemas();

    protected String getTargetDocId() {
        return getIntent().getStringExtra(DOCUMENT_ID);
    }

    protected Document fetchDocument(boolean forceRefresh)
            throws BusinessObjectUnavailableException {

        String docId = getTargetDocId();
        if (document == null) {
            document = (Document) getIntent().getSerializableExtra(DOCUMENT);
        }

        if (refresh || forceRefresh || document == null) {
            document = NuxeoAndroidServices.getInstance().getDocument(docId,
                    getSchemas(), refresh || forceRefresh);
        }
        return document;
    }

    protected void fetchIcon(Document targetDocument) {
        final String serverUrl = getSharedPreferences(
                "org.nuxeo.android.simpleclient_preferences", 0).getString(
                SettingsActivity.PREF_SERVER_URL, "");
        String urlImage = serverUrl + (serverUrl.endsWith("/") ? "" : "/")
                + targetDocument.getString("common:icon", "");
        ImageDownloader.getInstance().get(icon, urlImage, null,
                this.getHandler(),
                NuxeoAndroidApplication.CACHE_IMAGE_INSTRUCTIONS);
    }

    // *************
    protected void addToClipBoard(final String uuid) {

        AppPublics.THREAD_POOL.execute(this, new Runnable() {

            @Override
            public void run() {
                try {
                    NuxeoAndroidServices.getInstance().addToMyWorklist(uuid);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BaseDocumentViewActivity.this,
                                    "Document added to worklist",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    log.error("Error while adding to clipboard", e);
                    return;
                }
            }
        });
    }

    // ************* Download management

    protected void downloadAndDisplayBlob(final String flag) {

        AppPublics.THREAD_POOL.execute(this, new Runnable() {

            @Override
            public void run() {

                Blob downloadedBlob = null;
                try {
                    downloadedBlob = executeDownloadOperation(flag);
                } catch (BusinessObjectUnavailableException e) {
                    log.error("Error while getting file from server", e);
                    return;
                }

                if (downloadedBlob != null) {
                    final Blob blob = downloadedBlob;
                    final File downloadedFile = new File(getDownloadedFilePath(
                            downloadedBlob, flag));
                    byte[] buffer = new byte[4096];
                    try {
                        InputStream in = downloadedBlob.getStream();
                        OutputStream out = new FileOutputStream(downloadedFile);
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                        out.close();
                        in.close();
                    } catch (IOException e) {
                        log.error("Error while copying temporaty file", e);
                        return;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            displayDownloadedFile(downloadedFile,
                                    getDownloadedMimeType(blob, flag));
                        }
                    });
                }

            }

        });
    }

    protected Blob executeDownloadOperation(String flag)
            throws BusinessObjectUnavailableException {
        throw new UnsupportedOperationException(
                "The download operation is not set");
    }

    protected String getDownloadedFilePath(Blob blob, String flag) {
        return "/sdcard/NuxeoAndroid/nuxeo-downloaded-file.tmp";
    }

    protected String getDownloadedMimeType(Blob blob, String flag) {
        return blob.getMimeType();
    }

    protected void displayDownloadedFile(File tmpFile, String mimeTye) {

        Uri path = Uri.fromFile(tmpFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(path, mimeTye);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(this, "No Application Available to View " + mimeTye,
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onSynchronizeDisplayObjects() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTitleBarRefresh() {
        refresh = true;
        refreshBusinessObjectsAndDisplay(true);
    }
}
