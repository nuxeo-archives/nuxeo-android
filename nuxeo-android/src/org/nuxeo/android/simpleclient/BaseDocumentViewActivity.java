package org.nuxeo.android.simpleclient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.nuxeo.android.simpleclient.service.NuxeoAndroidServices;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.smartnsoft.droid4me.app.AppPublics;
import com.smartnsoft.droid4me.app.SmartActivity;
import com.smartnsoft.droid4me.app.AppPublics.BroadcastListener;
import com.smartnsoft.droid4me.app.AppPublics.BroadcastListenerProvider;
import com.smartnsoft.droid4me.app.AppPublics.SendLoadingIntent;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy;

public abstract  class BaseDocumentViewActivity extends
        SmartActivity<NuxeoAndroidApplication.TitleBarAggregate> implements
        BusinessObjectsRetrievalAsynchronousPolicy, SendLoadingIntent,
        BroadcastListenerProvider,
        NuxeoAndroidApplication.TitleBarShowHomeFeature,
        NuxeoAndroidApplication.TitleBarRefreshFeature {

    public static final String DOCUMENT_ID = "document_id";

    public static final String DOCUMENT = "document";

    protected boolean refresh = false;

    protected Document document = null;

    public BroadcastListener getBroadcastListener() {
        return new AppPublics.LoadingBroadcastListener(this, true) {
            @Override
            protected void onLoading(boolean isLoading) {
                getAggregate().getAttributes().toggleRefresh(isLoading);
            }
        };
    }

    protected abstract String getSchemas();

    protected Document fetchDocument(boolean forceRefresh) throws BusinessObjectUnavailableException {

        String docId = getIntent().getStringExtra(DOCUMENT_ID);

        if (document==null) {
            document = (Document) getIntent().getSerializableExtra(DOCUMENT);
        }

        if (refresh || forceRefresh || document==null) {
            document = NuxeoAndroidServices.getInstance().getDocument(docId, getSchemas(),true);
        }
        return document;
    }


    //************* Download management

    protected void downloadAndDisplayBlob() {

        AppPublics.THREAD_POOL.execute(this, new Runnable() {

            @Override
            public void run() {

                Blob downloadedBlob = null;
                try {
                    downloadedBlob = executeDownloadOperation();
                } catch (BusinessObjectUnavailableException e) {
                    log.error("Error while getting file from server", e);
                   return;
                }

                if (downloadedBlob!=null) {
                    final Blob blob = downloadedBlob;
                    final File downloadedFile = new File(getDownloadedFilePath(downloadedBlob));
                    byte[] buffer = new byte[4096];
                    try  {
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
                            displayDownloadedFile(downloadedFile, getDownloadedMimeType(blob));
                        }
                    });
                }

            }

        });
    }

    protected Blob executeDownloadOperation() throws BusinessObjectUnavailableException {
        throw new UnsupportedOperationException("The download operation is not set");
    }

    protected String getDownloadedFilePath(Blob blob) {
        return "/sdcard/NuxeoAndroid/nuxeo-downloaded-file.tmp";
    }

    protected String getDownloadedMimeType(Blob blob) {
        return blob.getMimeType();
    }

    protected void displayDownloadedFile(File tmpFile, String mimeTye) {

        Uri path = Uri.fromFile(tmpFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(path, mimeTye);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            startActivity(intent);
        }
        catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(this,
                "No Application Available to View " + mimeTye,
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
