package org.nuxeo.android.documentprovider;

import java.util.List;

import org.nuxeo.android.automationsample.R;
import org.nuxeo.android.fragments.BaseNuxeoFragment;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DocumentProviderSampleFragment extends BaseNuxeoFragment implements OnItemClickListener {

    protected ListView listView;

    protected TextView waitingMessage;

    protected View refreshBtn;
    
    protected ViewGroup mContainer;

    List<String> providerNames;

	public DocumentProviderSampleFragment() {
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.nxcp, container, false);
        waitingMessage = (TextView) v.findViewById(R.id.waitingMessage);
        refreshBtn = v.findViewById(R.id.refreshBtn);
        refreshBtn.setVisibility(View.INVISIBLE);
        listView = (ListView) v.findViewById(R.id.myList);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(this);
        mContainer = container;
        return v;
    }

	@Override
	protected boolean requireAsyncDataRetrieval() {
		return true;
	}
	
	protected void registerProviders() {

        Log.i(this.getClass().getSimpleName(), "register proviers .......");
        DocumentProvider docProvider = getAutomationClient().getDocumentProvider();

        // register a query
        String providerName1 = "Simple select";
        if (!docProvider.isRegistred(providerName1)) {
            String query = "select * from Document where ecm:mixinType != \"HiddenInNavigation\" AND ecm:isCheckedInVersion = 0 AND ecm:currentLifeCycleState != \"deleted\" order by dc:modified DESC";
            docProvider.registerNamedProvider(getNuxeoSession(), providerName1,
                    query, 10, false, false, null);
        }

        // register an operation
        String providerName2 = "Get Worklist operation";
        if (!docProvider.isRegistred(providerName2)) {
            // create the fetch operation
            OperationRequest getWorklistOperation = getNuxeoSession().newRequest(
                    "Seam.FetchFromWorklist");
            // define what properties are needed
            getWorklistOperation.setHeader("X-NXDocumentProperties",
                    "common,dublincore");
            // register provider from OperationRequest
            docProvider.registerNamedProvider(providerName2,
                    getWorklistOperation, null, false, false, null);
        }

        // register a documentList
        String providerName3 = "My Documents";
        if (!docProvider.isRegistred(providerName3)) {
            String query2 = "SELECT * FROM Document WHERE dc:contributors = ?";
            LazyUpdatableDocumentsList docList = new LazyUpdatableDocumentsListImpl(
                    getNuxeoSession(), query2,
                    new String[] { "Administrator" }, null, null, 10);
            docList.setName(providerName3);
            docProvider.registerNamedProvider(docList, false);
        }

        // register a query
        String providerName4 = "mypictures";
        if (!docProvider.isRegistred(providerName4)) {
            String query = "select * from Picture";
            docProvider.registerNamedProvider(getNuxeoSession(), providerName4,
                    query, 10, false, false, "image");
        }

        // register a query
        String providerName5 = "mynotes";
        if (!docProvider.isRegistred(providerName5)) {
            String query = "select * from Note";
            docProvider.registerNamedProvider(getNuxeoSession(), providerName5,
                    query, 10, false, false, "text");
        }

    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        // be sure we won't start with an empty list
        registerProviders();

        // get declared providers
        DocumentProvider docProvider = getAutomationClient().getDocumentProvider();
        providerNames = docProvider.listProviderNames();

        return true;
    }

    protected void onNuxeoDataRetrievalStarted() {
        waitingMessage.setText("Loading data ...");
        waitingMessage.setVisibility(View.VISIBLE);
        refreshBtn.setEnabled(false);
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        waitingMessage.setVisibility(View.INVISIBLE);
        refreshBtn.setEnabled(true);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, providerNames);
        listView.setAdapter(adapter);
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String providerName = providerNames.get(position);
		
		Bundle args = new Bundle();
		DocumentProviderViewFragment docProviderFrag = new DocumentProviderViewFragment();
		args.putString(DocumentProviderViewFragment.PROVIDER_NAME_PARAM, providerName);
		docProviderFrag.setArguments(args);
		
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(mContainer.getId(), docProviderFrag);
		transaction.addToBackStack(null);
		transaction.commit();
	}

}
