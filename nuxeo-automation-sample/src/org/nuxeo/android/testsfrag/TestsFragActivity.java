package org.nuxeo.android.testsfrag;

import org.nuxeo.android.automationsample.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;

public class TestsFragActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tests_frag);
	}
	
	public void Connect(View v) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, new ConnnectSampleFragment());
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void Fetch(View v) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, new SimpleFetchSampleFragment());
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void SimpleList(View v) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, new SimpleListFragment());
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void BrowseRepo(View v) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, new GetChildrenSampleFragment());
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.tests, menu);
//		return true;
//	}

}
