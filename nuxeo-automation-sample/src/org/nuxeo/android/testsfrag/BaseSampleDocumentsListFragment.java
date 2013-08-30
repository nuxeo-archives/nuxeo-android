package org.nuxeo.android.testsfrag;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.android.activities.BaseDocumentLayoutActivity;
import org.nuxeo.android.adapters.AbstractDocumentListAdapter;
import org.nuxeo.android.adapters.DocumentsListAdapter;
import org.nuxeo.android.automationsample.DocumentLayoutActivity;
import org.nuxeo.android.automationsample.R;
import org.nuxeo.android.documentprovider.LazyDocumentsList;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;
import org.nuxeo.android.fragments.BaseDocumentsListFragment;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class BaseSampleDocumentsListFragment extends BaseDocumentsListFragment {

	protected Document userHome;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.nxcp2, container, false);
        waitingMessage = (TextView)v.findViewById(R.id.waitingMessage);
        listView = (ListView) v.findViewById(R.id.myList);
        
		return v;
	}
    
    @Override
    protected Class<? extends BaseDocumentLayoutActivity> getEditActivityClass() {
        return DocumentLayoutActivity.class;
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



//    @Override
//    protected void setupViews() {
//        setContentView(R.layout.nxcp);
//        waitingMessage = (TextView) findViewById(R.id.waitingMessage);
//        refreshBtn = findViewById(R.id.refreshBtn);
//        listView = (ListView) findViewById(R.id.myList);
//    }

}
