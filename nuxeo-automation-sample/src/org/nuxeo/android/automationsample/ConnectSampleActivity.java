package org.nuxeo.android.automationsample;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.android.activities.BaseNuxeoActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class ConnectSampleActivity extends BaseNuxeoActivity {

	protected TextView statusText;
	protected Spinner spinner;
	protected List<String> opList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connect);

		statusText = (TextView) findViewById(R.id.statusText);
		statusText.setText("Connecting ...");

		spinner = (Spinner) findViewById(R.id.opList);
		spinner.setVisibility(Spinner.INVISIBLE);
	}

	@Override
	protected boolean requireAsyncDataRetrieval() {
		return true;
	}

	@Override
	protected Object retrieveNuxeoData() throws Exception {
		opList = new ArrayList<String>();
		opList.addAll(getNuxeoSession().getOperations().keySet());
		return true; // warn : returning null will disable the callback !!!
	}

	@Override
	protected void onNuxeoDataRetrieved(Object data) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, opList);
		adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setVisibility(0);
		statusText.setText("Connected : " + opList.size()
				+ "operations available");
	}

}
