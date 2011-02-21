package org.nuxeo.android.simpleclient;

import org.nuxeo.android.simpleclient.service.NuxeoAndroidServices;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

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
