package org.nuxeo.android.simpleclient;

import java.io.File;
import java.io.IOException;

import org.nuxeo.android.simpleclient.service.NuxeoAndroidServices;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

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
    }

}
