package org.nuxeo.android.automationsample;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class AutomationSampleActivity extends Activity implements View.OnClickListener{
    /** Called when the activity is first created. */


	public static final String TEST_SERVER = "http://android.demo.nuxeo.com/nuxeo/site/automation";
	public static final String TEST_USER = "droidUser";
	public static final String TEST_PASSWORD = "nuxeo4android";

	protected Button connectBtn;
	protected Spinner spinner;

	protected HttpAutomationClient nuxeoClient;
	protected Session nuxeoSession;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        connectBtn = (Button) findViewById(R.id.connect);
        connectBtn.setOnClickListener(this);

        spinner = (Spinner) findViewById(R.id.opList);
        spinner.setVisibility(4);
    }


    protected Session createNuxeoSession() {
    	if (nuxeoSession==null) {
    		 nuxeoClient = new HttpAutomationClient(TEST_SERVER);
    		 nuxeoSession = nuxeoClient.getSession(TEST_USER,TEST_PASSWORD);
    	}
    	return nuxeoSession;
    }

	@Override
	public void onClick(View view) {
		if (view == connectBtn) {

			final Activity activity = this;

			if (nuxeoSession==null) {
				// run connection in a separated thread to avoid freezing the UI in case of network lag
				Runnable initTask = new Runnable() {
					@Override
					public void run() {
						createNuxeoSession();
						// wait for UI thread to do the display
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								List<String> opList = new ArrayList<String>();
								opList.addAll(nuxeoSession.getOperations().keySet());
								ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,android.R.layout.simple_spinner_item,opList);
								adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
								spinner.setAdapter(adapter);
						        spinner.setVisibility(0);
							}
						});
					}
				};
				new Thread(initTask).start();
			}
		}

	}
}