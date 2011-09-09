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

public class HomeSampleActivity extends Activity implements View.OnClickListener{

	protected Button connectBtn;
	protected Button contentUriBtn;
	protected Button docListBtn;
	protected Button docProviderBtn;
	protected Button contentProviderBtn;

	protected Spinner spinner;

	protected List<String> opList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        connectBtn = (Button) findViewById(R.id.connect);
        connectBtn.setOnClickListener(this);

        contentUriBtn = (Button) findViewById(R.id.contentUriBtn);
        contentUriBtn.setOnClickListener(this);

        docListBtn = (Button) findViewById(R.id.docListBtn);
        docListBtn.setOnClickListener(this);

        docProviderBtn = (Button) findViewById(R.id.docProviderBtn);
        docProviderBtn.setOnClickListener(this);

        contentProviderBtn = (Button) findViewById(R.id.contentProviderBtn);
        contentProviderBtn.setOnClickListener(this);

        spinner = (Spinner) findViewById(R.id.opList);
        spinner.setVisibility(4);

        //imgTest.setImageURI(Uri.parse("content://nuxeo/blobs/5d72ce2a-2cbd-47b9-a0a5-93cb5fd56793"));
    }


	@Override
	public void onClick(View view) {
		if (view == connectBtn) {
          startActivity(new Intent(getApplicationContext(),
                    ConnectSampleActivity.class));
		}
		else if (view == contentUriBtn) {
            startActivity(new Intent(getApplicationContext(),
                    SimpleFetchSampleActivty.class));
		}
		else if (view == contentProviderBtn) {
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

	        case R.id.itemNetworkConfig:
	        	startActivity(new Intent(getApplicationContext(),
                    NetworkSettingsActivity.class));
	        	break;
	        case R.id.itemServerSettings:
	        	startActivity(new Intent(getApplicationContext(),
                    ServerSettingsActivity.class));
	        	break;
	        }
	        return super.onOptionsItemSelected(item);
	 }


}