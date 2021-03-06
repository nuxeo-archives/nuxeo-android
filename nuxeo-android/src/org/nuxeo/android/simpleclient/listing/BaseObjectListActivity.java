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
package org.nuxeo.android.simpleclient.listing;

import org.nuxeo.android.simpleclient.R;
import org.nuxeo.android.simpleclient.ui.TitleBarAggregate;
import org.nuxeo.android.simpleclient.ui.TitleBarRefreshFeature;
import org.nuxeo.android.simpleclient.ui.TitleBarShowHomeFeature;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.smartnsoft.droid4me.app.AppPublics;
import com.smartnsoft.droid4me.app.AppPublics.BroadcastListener;
import com.smartnsoft.droid4me.app.WrappedSmartListActivity;
import com.smartnsoft.droid4me.framework.DetailsProvider.ObjectEvent;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy;

public abstract class BaseObjectListActivity extends
        WrappedSmartListActivity<TitleBarAggregate> implements
        TitleBarShowHomeFeature, TitleBarRefreshFeature,
        AppPublics.BroadcastListenerProvider,
        BusinessObjectsRetrievalAsynchronousPolicy,
        AppPublics.SendLoadingIntent {

    protected boolean fromCache = true;

    public BaseObjectListActivity() {
        super();
    }

    public abstract Intent handleEventOnListItem(Activity activity,
            Object viewAttributes, View view, Object obj,
            ObjectEvent objectEvent);

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