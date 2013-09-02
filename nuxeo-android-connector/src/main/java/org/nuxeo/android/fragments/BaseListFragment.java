package org.nuxeo.android.fragments;

import org.nuxeo.android.fragments.BaseNuxeoFragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public abstract class BaseListFragment extends BaseNuxeoFragment implements OnItemClickListener {

	protected ListView listView;

    protected TextView waitingMessage;

//  protected View refreshBtn;

    protected boolean refresh = false;

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        if (listView != null) {
            registerForContextMenu(listView);
            listView.setOnItemClickListener(this);
        }
//        if(Build.VERSION.SDK_INT >= 11) {
//        	refreshBtn.setVisibility(View.GONE);
//        } else
//        {
//	        if (refreshBtn != null) {
//	            refreshBtn.setOnClickListener(this);
//	        }
//        }
    }
//
//    protected abstract void setupViews();

    protected void setupViewsOnDataLoading() {
        if (waitingMessage != null) {
            waitingMessage.setText("Loading data ...");
            waitingMessage.setVisibility(View.VISIBLE);
        }
//        if (refreshBtn != null) {
//            refreshBtn.setEnabled(false);
//        }
    }

    protected void setupViewsOnDataLoaded() {
        if (waitingMessage != null) {
            waitingMessage.setVisibility(View.GONE);
        }
//        if (refreshBtn != null) {
//            refreshBtn.setEnabled(true);
//        }
    }

    @Override
    protected void onNuxeoDataRetrievalStarted() {
        setupViewsOnDataLoading();
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        setupViewsOnDataLoaded();
    }

    @Override
    protected void onNuxeoDataRetrieveFailed() {
        setupViewsOnDataLoaded();
    }
    
//    @Override
//    public void onClick(View view) {
//        if (view == refreshBtn) {
//            doRefresh();
//        }
//    }

    @Override
    public void onItemClick(AdapterView<?> list, View container, int position,
            long id) {
        onListItemClicked(position);
    }

    protected abstract void onListItemClicked(int listItemPosition);

    protected abstract void doRefresh();

}
