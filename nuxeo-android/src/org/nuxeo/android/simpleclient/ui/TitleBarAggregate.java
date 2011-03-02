package org.nuxeo.android.simpleclient.ui;

import org.nuxeo.android.simpleclient.HomeActivity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.smartnsoft.droid4me.app.AppPublics;

public class TitleBarAggregate extends AppPublics.LoadingBroadcastListener implements View.OnClickListener{

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

    public void setOnRefresh(
            TitleBarRefreshFeature titleBarRefreshFeature) {
        this.onRefresh = titleBarRefreshFeature;
        attributes.setShowRefresh(this);
    }

    public void setOnSearch(
            TitleBarShowSearchFeature titleBarShowSearchFeature) {
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
