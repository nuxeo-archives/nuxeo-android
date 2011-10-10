package org.nuxeo.android.activities;


import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Simple base class for sharing UI code between list view activities.
 * (one of the target is to hide as much as possible of the UI code that is not related to Nuxeo SDK itself)
 *
 * @author tiry
 *
 */
public abstract class BaseListActivity extends BaseNuxeoActivity implements
		View.OnClickListener {

	protected ListView listView;
	protected TextView waitingMessage;
	protected Button refreshBtn;

	public BaseListActivity() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupViews();
		if (listView != null) {
			registerForContextMenu(listView);
		}
		if (refreshBtn != null) {
			refreshBtn.setOnClickListener(this);
		}
	}

	protected abstract void setupViews();

	protected void setupViewsOnDataLoading() {
		if (waitingMessage != null) {
			waitingMessage.setText("Loading data ...");
			waitingMessage.setVisibility(View.VISIBLE);
		}
		if (refreshBtn != null) {
			refreshBtn.setGravity(Gravity.RIGHT);
			refreshBtn.setEnabled(false);
		}
	}

	protected void setupViewsOnDataLoaded() {
		if (waitingMessage != null) {
			waitingMessage.setVisibility(View.INVISIBLE);
		}
		if (refreshBtn != null) {
			refreshBtn.setEnabled(true);
		}
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
	public boolean onCreateOptionsMenu(Menu menu) {
		populateMenu(menu);
		return super.onCreateOptionsMenu(menu);
	}

	protected abstract void populateMenu(Menu menu);

	@Override
	public void onClick(View arg0) {
		doRefresh();
	}

	protected abstract void doRefresh();

	@Override
	protected boolean requireAsyncDataRetrieval() {
		return true;
	}

}