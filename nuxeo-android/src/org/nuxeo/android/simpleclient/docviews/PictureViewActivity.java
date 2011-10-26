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

import java.io.IOException;

import org.json.JSONException;
import org.nuxeo.android.simpleclient.R;
import org.nuxeo.android.simpleclient.forms.LinearFormManager;
import org.nuxeo.android.simpleclient.service.NuxeoAndroidServices;
import org.nuxeo.android.simpleclient.ui.TitleBarRefreshFeature;
import org.nuxeo.android.simpleclient.ui.TitleBarShowHomeFeature;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smartnsoft.droid4me.app.AppPublics.BroadcastListenerProvider;
import com.smartnsoft.droid4me.app.AppPublics.SendLoadingIntent;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy;

public class PictureViewActivity extends BaseDocumentViewActivity implements
        BusinessObjectsRetrievalAsynchronousPolicy, SendLoadingIntent,
        BroadcastListenerProvider, TitleBarShowHomeFeature,
        TitleBarRefreshFeature {

    private Blob blob;

    private ImageView picture;

    private TextView title;

    private ImageButton downloadAction;

    private LinearLayout formLayout;

    private ImageButton documentBtn;

    protected static String PICTURE_FIELDS = "[ { xpath : 'imd:orientation', label : 'Orientation'}, "
            + " { xpath : 'imd:equipment', label : 'Equipment'}, "
            + " { xpath : 'imd:date_time_original', label : 'Date'},"
            + " { xpath : 'imd:pixel_xdimension', label : 'Original width'},"
            + " { xpath : 'imd:pixel_ydimension', label : 'Original height'} ]";

    @Override
    protected String getSchemas() {
        return "dublincore,common,image_metadata";
    }

    @Override
    public void onFulfillDisplayObjects() {
        if (blob != null) {
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(blob.getStream());
                picture.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        title.setText(document.getTitle());
        downloadAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PictureViewActivity.this,
                        "started Picture Download", Toast.LENGTH_SHORT).show();
                downloadAndDisplayBlob(null);
            }
        });
        documentBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PictureViewActivity.this,
                        DocumentViewActivity.class).putExtra(
                        BaseDocumentViewActivity.DOCUMENT, document));
            }
        });

        try {
            LinearFormManager.displayForm(this, formLayout, document,
                    PICTURE_FIELDS, false);
            formLayout.refreshDrawableState();
        } catch (JSONException e) {
            log.error("Error during form generation", e);
        }
    }

    @Override
    public void onRetrieveBusinessObjects()
            throws BusinessObjectUnavailableException {
        if (blob == null) {
            String docId = getIntent().getStringExtra(DOCUMENT_ID);
            blob = NuxeoAndroidServices.getInstance().getPictureView(docId,
                    "Medium", refresh, true);
        }
        if (document == null
                || refresh
                || !document.getProperties().map().containsKey(
                        "imd:icc_profile")) {
            fetchDocument(true);
        }
        fetchIcon(document);
    }

    @Override
    public void onRetrieveDisplayObjects() {
        setContentView(R.layout.picture_view_layout);
        picture = (ImageView) findViewById(R.id.pictureView);
        title = (TextView) findViewById(R.id.title);
        downloadAction = (ImageButton) findViewById(R.id.downloadBtn);
        icon = (ImageView) findViewById(R.id.icon);
        formLayout = (LinearLayout) findViewById(R.id.linearDocumentLayout);
        documentBtn = (ImageButton) findViewById(R.id.documentBtn);
    }

    @Override
    protected Blob executeDownloadOperation(String flag)
            throws BusinessObjectUnavailableException {
        return NuxeoAndroidServices.getInstance().getPictureView(
                document.getId(), "OriginalJpeg", refresh, true);
    }

    @Override
    protected String getDownloadedMimeType(Blob blob, String flag) {
        return "image/jpeg";
    }

}
