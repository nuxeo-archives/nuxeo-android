package org.nuxeo.android.appraisal.fragments;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.android.adapters.AbstractDocumentListAdapter;
import org.nuxeo.android.adapters.DocumentsListAdapter;
import org.nuxeo.android.appraisal.R;
import org.nuxeo.android.documentprovider.LazyDocumentsList;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;
import org.nuxeo.android.fragments.BaseDocLayoutFragAct;
import org.nuxeo.android.fragments.BaseDocumentsListFragment;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class AppraisalListFragment extends BaseDocumentsListFragment {

	protected static final int MNU_CONFIG = 20;

	protected static final int MNU_NETWORK_CONFIG = 21;

	protected static final int MNU_SERVER_CONFIG = 22;

	protected static final int CTXMNU_VIEW_PICTURES = 10;

	protected static final int CTXMNU_VALIDATE = 20;

	protected static final int CTXMNU_DELETE = 30;

	protected static final int REQUEST_NETWORK = 1;

	protected static final int REQUEST_SERVER = 1;

	public AppraisalListFragment() {
	}
	
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
    	View v = inflater.inflate(R.layout.listview_layout, container, false);

        waitingMessage = (TextView)v.findViewById(R.id.waitingMessage);
        listView = (ListView) v.findViewById(R.id.myList);
        return v;
    }

	@Override
	protected LazyUpdatableDocumentsList fetchDocumentsList(byte cacheParam,
			String order) throws Exception {
		if (order.equals("")) {
			order = " order by dc:modified desc";
		}
		String user = getNuxeoContext().getSession().getLogin().getUsername();
        Documents docs = getNuxeoContext().getDocumentManager().query(
                "select * from Appraisal where appraisal:assignee=? AND ecm:currentLifeCycleState=?" + order,
                new String[] { user, "assigned" }, null,
                "common,dublincore,appraisal", 0, 10, cacheParam);
        if (docs != null) {
            return docs.asUpdatableDocumentsList();
        }
        throw new RuntimeException("fetch Operation did return null");
	}

	@Override
	protected void displayDocumentList(ListView listView,
			LazyDocumentsList documentsList) {
		AbstractDocumentListAdapter adapter = new DocumentsListAdapter(getActivity(),
				documentsList, R.layout.list_item, getMapping());
		listView.setAdapter(adapter);
	}

	protected Map<Integer, String> getMapping() {

		Map<Integer, String> mapping = new HashMap<Integer, String>();
		mapping.put(R.id.icon, "iconUri");
		mapping.put(R.id.title_entry, "dc:title");
		mapping.put(R.id.status_entry, "status");
		mapping.put(R.id.client, "appraisal:customerName");
		mapping.put(R.id.declaration_date, "(date)dc:created");
		mapping.put(R.id.visite_date, "(date)appraisal:date_of_visit");
		return mapping;
	}

	@Override
	protected Document initNewDocument(String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Class<? extends BaseDocLayoutFragAct> getEditActivityClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onListItemClicked(int listItemPosition) {

		Document selectedDocument = documentsList.getDocument(listItemPosition);
		Bundle args = new Bundle();
		args.putSerializable(AppraisalContentListFragment.ROOT_DOC_PARAM,
				selectedDocument);
		AppraisalContentListFragment fragment = new AppraisalContentListFragment();
		fragment.setArguments(args);

		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.list_frag_container, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

}
