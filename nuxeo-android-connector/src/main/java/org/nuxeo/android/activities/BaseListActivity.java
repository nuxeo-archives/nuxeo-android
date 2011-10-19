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

package org.nuxeo.android.activities;


import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Simple base class for sharing UI code between list view activities.
 * (one of the target is to hide as much as possible of the UI code that is not related to Nuxeo SDK itself)
 *
 * @author tiry
 *
 */
public abstract class BaseListActivity extends BaseNuxeoActivity implements
		View.OnClickListener, OnItemClickListener {

	protected ListView listView;
	protected TextView waitingMessage;
	protected View refreshBtn;

	public BaseListActivity() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupViews();
		if (listView != null) {
			registerForContextMenu(listView);
			listView.setOnItemClickListener(this);
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
	protected void onNuxeoDataRetrieveFailed() {
		setupViewsOnDataLoaded();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		populateMenu(menu);
		return super.onCreateOptionsMenu(menu);
	}

	protected abstract void populateMenu(Menu menu);

	@Override
	public void onClick(View view) {
		if (view == refreshBtn) {
			doRefresh();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> list, View container, int position, long id) {
		onListItemClicked(position);
	}

	protected abstract void onListItemClicked(int listItemPosition);

	protected abstract void doRefresh();

	@Override
	protected boolean requireAsyncDataRetrieval() {
		return true;
	}

}