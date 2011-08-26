package org.nuxeo.android.automationsample;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.android.context.NuxeoContext;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class AutomationSampleActivity extends Activity implements View.OnClickListener{

	protected Button connectBtn;
	protected Button cpBtn;
	protected Button cursorBtn;

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

        cursorBtn = (Button) findViewById(R.id.cursorBtn);
        cursorBtn.setOnClickListener(this);

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
		else if (view == cursorBtn) {
            startActivity(new Intent(getApplicationContext(),
                    CursorSampleActivity.class));
		}
		else {
			// start activity
			// See openIntend
			startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), 0);
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			Uri uri = data.getData();
			//getContentResolver().openAssetFileDescriptor(uri, "r").createInputStream();
			//setResult(resultCode, data)
		}

		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.simplemenu, menu);
		return super.onCreateOptionsMenu(menu);
	}


	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	        // Handle all of the possible menu actions.
	        switch (item.getItemId()) {

	        case R.id.itemOfflineConfig:
	        	startActivity(new Intent(getApplicationContext(),
                    NetworkSettingsActivity.class));
	        	break;
	        }
	        return super.onOptionsItemSelected(item);
	 }


}