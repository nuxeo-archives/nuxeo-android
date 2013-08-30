package org.nuxeo.android.testsfrag;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.android.automationsample.R;
import org.nuxeo.android.fragments.BaseNuxeoFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ConnnectSampleFragment extends BaseNuxeoFragment {


    protected TextView statusText;

    protected ListView listView;

    protected List<String> opList = new ArrayList<String>();
    

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.loading, container, false);
        statusText = (TextView) v.findViewById(R.id.statusTextFrag);
        statusText.setText("Connecting ...");
        listView = (ListView) v.findViewById(R.id.listViewFrag);
		return v;
	}

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        opList = new ArrayList<String>();
        opList.addAll(getNuxeoSession().getOperations().keySet());
        return true; // warn : returning null will disable the callba	ck !!!
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(),
                android.R.layout.simple_list_item_1, opList);
    	listView.setAdapter(adapter);
      statusText.setText("Connected : " + opList.size()
      + "operations available");
    }

	@Override
	protected boolean requireAsyncDataRetrieval() {
		return true;
	}
}
