package org.nuxeo.android.simpleclient;

import com.smartnsoft.droid4me.app.AppPublics;
import com.smartnsoft.droid4me.app.SmartActivity;
import com.smartnsoft.droid4me.app.AppPublics.BroadcastListener;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy;

public final class DocumentViewActivity extends
        SmartActivity<NuxeoAndroidApplication.TitleBarAggregate> implements
        BusinessObjectsRetrievalAsynchronousPolicy,
        AppPublics.SendLoadingIntent, AppPublics.BroadcastListenerProvider,
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

    @Override
    public void onRetrieveDisplayObjects() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRetrieveBusinessObjects()
            throws BusinessObjectUnavailableException {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFulfillDisplayObjects() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSynchronizeDisplayObjects() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTitleBarRefresh() {
        refreshBusinessObjectsAndDisplay(true);
    }

}
