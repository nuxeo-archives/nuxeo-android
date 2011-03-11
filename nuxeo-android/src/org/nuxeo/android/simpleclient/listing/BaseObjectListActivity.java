package org.nuxeo.android.simpleclient.listing;

import org.nuxeo.android.simpleclient.R;
import org.nuxeo.android.simpleclient.ui.TitleBarAggregate;
import org.nuxeo.android.simpleclient.ui.TitleBarRefreshFeature;
import org.nuxeo.android.simpleclient.ui.TitleBarShowHomeFeature;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.smartnsoft.droid4me.app.AppPublics;
import com.smartnsoft.droid4me.app.WrappedSmartListActivity;
import com.smartnsoft.droid4me.app.AppPublics.BroadcastListener;
import com.smartnsoft.droid4me.framework.DetailsProvider.ObjectEvent;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy;

public abstract class BaseObjectListActivity extends
        WrappedSmartListActivity<TitleBarAggregate> implements
        TitleBarShowHomeFeature, TitleBarRefreshFeature,
        AppPublics.BroadcastListenerProvider,BusinessObjectsRetrievalAsynchronousPolicy,
        AppPublics.SendLoadingIntent {

    protected boolean fromCache = true;

    public BaseObjectListActivity() {
        super();
    }

    public abstract Intent handleEventOnListItem(Activity activity, Object viewAttributes, View view, Object obj, ObjectEvent objectEvent);

    @Override
    public BroadcastListener getBroadcastListener() {
        return new AppPublics.LoadingBroadcastListener(this, true) {
            @Override
            protected void onLoading(boolean isLoading) {
                getAggregate().getAttributes().toggleRefresh(isLoading);
            }
        };
    }

    @Override
    public void onFulfillDisplayObjects() {
        super.onFulfillDisplayObjects();

        getSmartListView().getListView().setEmptyView(
                getLayoutInflater().inflate(R.layout.empty_list_view, null));
    }

    @Override
    public void onTitleBarRefresh() {
        fromCache = false;
        refreshBusinessObjectsAndDisplayAndNotifyBusinessObjectsChanged(false);
    }

}