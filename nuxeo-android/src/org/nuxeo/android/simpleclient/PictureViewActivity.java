package org.nuxeo.android.simpleclient;

import java.io.IOException;

import org.nuxeo.android.simpleclient.service.NuxeoAndroidServices;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartnsoft.droid4me.app.AppPublics.BroadcastListenerProvider;
import com.smartnsoft.droid4me.app.AppPublics.SendLoadingIntent;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy;

public class PictureViewActivity extends BaseDocumentViewActivity implements
        BusinessObjectsRetrievalAsynchronousPolicy, SendLoadingIntent,
        BroadcastListenerProvider,
        NuxeoAndroidApplication.TitleBarShowHomeFeature,
        NuxeoAndroidApplication.TitleBarRefreshFeature {

    private Blob blob;
    private ImageView picture;
    private TextView description;
    private TextView title;
    private Button downloadAction;

    @Override
    protected String getSchemas() {
        return NuxeoAndroidServices.DEFAULT_SCHEMAS;
    }

    @Override
    public void onFulfillDisplayObjects() {
        if (blob!=null) {
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(blob.getStream());
                picture.setImageBitmap(bitmap);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        title.setText(document.getTitle());
        description.setText(document.getString("dc:description", ""));
        downloadAction.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(PictureViewActivity.this,
                        "started Picture Download",
                        Toast.LENGTH_SHORT).show();
                downloadAndDisplayBlob();
            }
        });
    }

    @Override
    public void onRetrieveBusinessObjects()
            throws BusinessObjectUnavailableException {
        if (blob==null) {
            String docId = getIntent().getStringExtra(DOCUMENT_ID);
            blob = NuxeoAndroidServices.getInstance().getPictureView(docId, "Medium", refresh, true);
        }
        if (document==null) {
            document = (Document) getIntent().getSerializableExtra(DOCUMENT);
        }
    }

    @Override
    public void onRetrieveDisplayObjects() {
        setContentView(R.layout.picture_view_layout);
        picture = (ImageView) findViewById(R.id.pictureView);
        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        downloadAction = (Button) findViewById(R.id.downloadBtn);
    }

    @Override
    protected Blob executeDownloadOperation() throws BusinessObjectUnavailableException {
        return NuxeoAndroidServices.getInstance().getPictureView(document.getId(),"OriginalJpeg", refresh, true);
    }

    @Override
    protected String getDownloadedMimeType(Blob blob) {
        return "image/jpeg";
    }

}
