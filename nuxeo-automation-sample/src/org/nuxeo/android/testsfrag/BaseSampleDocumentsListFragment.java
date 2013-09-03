package org.nuxeo.android.testsfrag;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.android.activities.BaseDocumentLayoutActivity;
import org.nuxeo.android.adapters.AbstractDocumentListAdapter;
import org.nuxeo.android.adapters.DocumentsListAdapter;
import org.nuxeo.android.automationsample.R;
import org.nuxeo.android.documentprovider.LazyDocumentsList;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;
import org.nuxeo.android.fragments.BaseDocLayoutFragAct;
import org.nuxeo.android.fragments.BaseDocumentsListFragment;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class BaseSampleDocumentsListFragment extends BaseDocumentsListFragment {

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
	
	protected Callback mCallback;
	
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
	protected LazyUpdatableDocumentsList fetchDocumentsList(byte cacheParam)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	public interface Callback {
		/**
		 * Callback for when an item has been selected.
		 */
		public void viewDocument(LazyUpdatableDocumentsList documentsList, int id);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callback)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallback = (Callback) activity;
	}
	
	@Override
	public void onListItemClicked(int position) {
		mCallback.viewDocument(documentsList, position);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
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
//	}
}
