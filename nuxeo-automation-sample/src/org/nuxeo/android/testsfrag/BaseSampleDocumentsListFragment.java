package org.nuxeo.android.testsfrag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.nuxeo.android.activities.BaseDocumentLayoutActivity;
import org.nuxeo.android.adapters.AbstractDocumentListAdapter;
import org.nuxeo.android.adapters.DocumentsListAdapter;
import org.nuxeo.android.automationsample.R;
import org.nuxeo.android.documentprovider.LazyDocumentsList;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;
import org.nuxeo.android.fragments.BaseDocLayoutFragAct;
import org.nuxeo.android.fragments.BaseDocumentsListFragment;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.impl.NotAvailableOffline;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public abstract class BaseSampleDocumentsListFragment extends BaseDocumentsListFragment {

	protected Document userHome;
	
	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;
	
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";
		
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.nxcp2, container, false);
        waitingMessage = (TextView)v.findViewById(R.id.waitingMessage);
        listView = (ListView) v.findViewById(R.id.myList);
        
		return v;
	}
    
    @Override
    protected Class<? extends BaseDocLayoutFragAct> getEditActivityClass() {
        return DocumentLayoutFragActivity.class;
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        // fetch User's home
        userHome = getNuxeoContext().getDocumentManager().getUserHome();
        // let base class fetch the list
        return super.retrieveNuxeoData();
    }

    protected Map<Integer, String> getMapping() {
        Map<Integer, String> mapping = new HashMap<Integer, String>();
        mapping.put(R.id.title_entry, "dc:title");
        mapping.put(R.id.status_entry, "status");
        mapping.put(R.id.iconView, "iconUri");
        mapping.put(R.id.description, "dc:description");
        mapping.put(R.id.id_entry, "uuid");
        return mapping;
    }
	
    @Override
    protected void displayDocumentList(ListView listView,
            LazyDocumentsList documentsList) {
        AbstractDocumentListAdapter adapter = new DocumentsListAdapter(getActivity().getBaseContext(),
                documentsList, R.layout.list_item, getMapping());
        listView.setAdapter(adapter);
    }

    @Override
    protected Document initNewDocument(String type) {
        return new Document(userHome.getPath(), "newAndroidDoc", type);
    }
    
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}
	

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.clear();
		if (Build.VERSION.SDK_INT >= 11) {
			SubMenu subMenu = menu.addSubMenu(Menu.NONE, MNU_SORT, 0,
					"sort");
			subMenu.add(Menu.NONE, MNU_SORT + 1, 0, "A - z");
			subMenu.add(Menu.NONE, MNU_SORT + 2, 1, "z - A");
			subMenu.add(Menu.NONE, MNU_SORT + 3, 2, "last modification up");
			subMenu.add(Menu.NONE, MNU_SORT + 4, 3, "last modification down");
			subMenu.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			menu.add(Menu.NONE, MNU_REFRESH, 1, "Refresh").setShowAsAction(
					MenuItem.SHOW_AS_ACTION_IF_ROOM);
		} else {
			SubMenu subMenu = menu.addSubMenu(Menu.NONE, MNU_SORT, 0,
					"New item");
			menu.add(Menu.NONE, MNU_SORT, 0, "Sort");
			subMenu.add(Menu.NONE, MNU_SORT + 1, 0, "A - Z");
			subMenu.add(Menu.NONE, MNU_SORT + 2, 1, "Z - A");
			subMenu.add(Menu.NONE, MNU_SORT + 3, 2, "last modification up");
			subMenu.add(Menu.NONE, MNU_SORT + 4, 3, "last modification down");
			menu.add(Menu.NONE, MNU_REFRESH, 1, "Refresh");
		}
	}


//	/**
//	 * Turns on activate-on-click mode. When this mode is on, list items will be
//	 * given the 'activated' state when touched.
//	 */
//	public void setActivateOnItemClick(boolean activateOnItemClick) {
//		// When setting CHOICE_MODE_SINGLE, ListView will automatically
//		// give items the 'activated' state when touched.
//		listView.setChoiceMode(
//				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
//						: ListView.CHOICE_MODE_NONE);
//	}
//
//	private void setActivatedPosition(int position) {
//		if (position == ListView.INVALID_POSITION) {
//			listView.setItemChecked(mActivatedPosition, false);
//		} else {
//			listView.setItemChecked(position, true);
//		}
//
//		mActivatedPosition = position;
// }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MNU_REFRESH:
			doRefresh();
			break;
		case MNU_SORT + 1:
			new NuxeoListAsyncTask().execute(" order by dc:title asc");
			break;
		case MNU_SORT + 2:
			new NuxeoListAsyncTask().execute(" order by dc:title desc");
			break;
		case MNU_SORT + 3:
			new NuxeoListAsyncTask().execute(" order by dc:modified desc");
			break;
		case MNU_SORT + 4:
			new NuxeoListAsyncTask().execute(" order by dc:modified");
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	protected abstract String getBaseQuery();
	
	
	protected LazyUpdatableDocumentsList fetchDocumentsList(byte cacheParam,
			String order) throws Exception {
		if (order.equals("")) {
			order = " order by dc:modified desc";
		}
		Documents docs = getNuxeoContext().getDocumentManager().query(
				getBaseQuery() + order, null, null, null, 0, 10, cacheParam);
		if (docs != null) {
			return docs.asUpdatableDocumentsList();
		}
		throw new RuntimeException("fetch Operation did return null");
	}

	protected Object retrieveNuxeoData(String order) throws Exception {
		byte cacheParam = CacheBehavior.STORE;
		if (refresh) {
			cacheParam = (byte) (cacheParam | CacheBehavior.FORCE_REFRESH);
			refresh = false;
		}
		return fetchDocumentsList(cacheParam, order);
	}

	protected class NuxeoListAsyncTask extends
			AsyncTask<String, Integer, Object> {

		@Override
		protected void onPreExecute() {
			loadingInProgress = true;
			onNuxeoDataRetrievalStarted();
			super.onPreExecute();
		}

		@Override
		protected Object doInBackground(String... arg0) {
			try {
				Object result = retrieveNuxeoData(arg0[0]);
				return result;
			} catch (NotAvailableOffline naoe) {
				BaseSampleDocumentsListFragment.this.getActivity().runOnUiThread(
						new Runnable() {
							@Override
							public void run() {
								Toast.makeText(
										getActivity().getBaseContext(),
										"This screen can bot be displayed offline",
										Toast.LENGTH_LONG).show();
							}
						});
				return null;
			} catch (Exception e) {
				Log.e("NuxeoAsyncTask",
						"Error while executing async Nuxeo task in activity", e);
				try {
					cancel(true);
				} catch (Throwable t) {
					// NOP
				}
				return null;
			}
		}

		@Override
		protected void onPostExecute(Object result) {
			loadingInProgress = false;
			if (result != null) {
				onNuxeoDataRetrieved(result);
			} else {
				onNuxeoDataRetrieveFailed();
			}
		}
	}
}
