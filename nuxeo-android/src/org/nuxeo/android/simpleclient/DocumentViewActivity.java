package org.nuxeo.android.simpleclient;

import org.nuxeo.android.simpleclient.service.NuxeoAndroidServices;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartnsoft.droid4me.app.AppPublics;
import com.smartnsoft.droid4me.app.AppPublics.BroadcastListener;
import com.smartnsoft.droid4me.app.AppPublics.BroadcastListenerProvider;
import com.smartnsoft.droid4me.app.AppPublics.SendLoadingIntent;
import com.smartnsoft.droid4me.app.SmartActivity;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy;

public final class DocumentViewActivity extends
        SmartActivity<NuxeoAndroidApplication.TitleBarAggregate> implements
        BusinessObjectsRetrievalAsynchronousPolicy, SendLoadingIntent,
        BroadcastListenerProvider,
        NuxeoAndroidApplication.TitleBarShowHomeFeature,
        NuxeoAndroidApplication.TitleBarRefreshFeature {

    public BroadcastListener getBroadcastListener() {
        return new AppPublics.LoadingBroadcastListener(this, true) {
            @Override
            protected void onLoading(boolean isLoading) {
                getAggregate().getAttributes().toggleRefresh(isLoading);
            }
        };
    }

    enum SOURCE_ACTIVITY_ENUM {
        MyDocument, SearchDocument
    };

    public final static int MY_DOCUMENT = 0;

    public final static int SEARCH = 1;

    public static final String DOCUMENT_ID = "document_id";

    public static final String DOCUMENT = "document";

    public static final String SOURCE_ACTIVITY = "source_activity";

    private TextView description;

    private TextView title;

    private Document document = null;

    private LinearLayout layout;

    protected boolean refresh = false;

    @Override
    public void onRetrieveDisplayObjects() {

        setContentView(R.layout.document_view_layout);
        layout = (LinearLayout) findViewById(R.id.documentLayout);
        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
    }

    @Override
    public void onRetrieveBusinessObjects()
            throws BusinessObjectUnavailableException {

        String docId = getIntent().getStringExtra(DOCUMENT_ID);

        if (document==null) {
            document = (Document) getIntent().getSerializableExtra(DOCUMENT);
        }

        if (refresh || document==null) {
            document = NuxeoAndroidServices.getInstance().getDocument(docId, true);
        }
    }

    @Override
    public void onFulfillDisplayObjects() {

        if (document != null) {
            title.setText(document.getTitle());
            description.setText(document.getString("dc:description", ""));
            for (String key : document.getProperties().getKeys()) {
                final TextView textView = new TextView(this);

                if (key != null) {
                    try {
                        final String value = document.getString(key, "");
                        if (value != null) {
                            textView.setText(key + " => " + value);
                            final int padding = getResources().getDimensionPixelSize(
                                    R.dimen.defaultPadding);
                            textView.setPadding(padding, padding, padding,
                                    padding);
                            layout.addView(textView);
                        }
                    } catch (Exception exception) {
                        if (log.isWarnEnabled()) {
                            log.warn("NULL", exception);
                        }
                    }
                }
            }
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
