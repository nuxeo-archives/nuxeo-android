package org.nuxeo.android.automationsample;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.android.context.NuxeoContext;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class AutomationSampleActivity extends Activity implements View.OnClickListener{

	protected Button connectBtn;
	protected Button cpBtn;

	protected Spinner spinner;

	protected List<String> opList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        connectBtn = (Button) findViewById(R.id.connect);
        connectBtn.setOnClickListener(this);

        cpBtn = (Button) findViewById(R.id.cp);
        cpBtn.setOnClickListener(this);

        spinner = (Spinner) findViewById(R.id.opList);
        spinner.setVisibility(4);
    }


	@Override
	public void onClick(View view) {
		if (view == connectBtn) {

			final Activity activity = this;

			if (opList==null) {
				// run connection in a separated thread to avoid freezing the UI in case of network lag
				Runnable initTask = new Runnable() {
					@Override
					public void run() {

						opList = new ArrayList<String>();
						opList.addAll(NuxeoContext.get(activity.getApplication()).getSession().getOperations().keySet());

						// wait for UI thread to do the display
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
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
		else if (view == cpBtn) {
            startActivity(new Intent(getApplicationContext(),
                    ContentProviderSampleActivity.class));
		}

	}
}