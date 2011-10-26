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
package org.nuxeo.android.simpleclient.ui;

import org.nuxeo.android.simpleclient.HomeActivity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.smartnsoft.droid4me.app.AppPublics;

public class TitleBarAggregate extends AppPublics.LoadingBroadcastListener
        implements View.OnClickListener {

    public final boolean customTitleSupported;

    public TitleBarAttributes attributes;

    private TitleBarRefreshFeature onRefresh;

    private TitleBarShowSearchFeature onSearch;

    public TitleBarAggregate(Activity activity, boolean customTitleSupported) {
        super(activity, true);
        this.customTitleSupported = customTitleSupported;
    }

    public TitleBarAttributes getAttributes() {
        return attributes;
    }

    public void setOnRefresh(TitleBarRefreshFeature titleBarRefreshFeature) {
        this.onRefresh = titleBarRefreshFeature;
        attributes.setShowRefresh(this);
    }

    public void setOnSearch(TitleBarShowSearchFeature titleBarShowSearchFeature) {
        this.onSearch = titleBarShowSearchFeature;
        attributes.setShowSearch(true, this);
    }

    @Override
    protected void onLoading(boolean isLoading) {
        attributes.toggleRefresh(isLoading);
    }

    public void onClick(View view) {
        if (view == attributes.home) {
            getActivity().startActivity(
                    new Intent(getActivity(), HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            getActivity().finish();
        } else if (view == attributes.refresh) {
            onRefresh.onTitleBarRefresh();
        } else if (view == attributes.search) {
            onSearch.onTitleBarSearch();
        }
    }
}
