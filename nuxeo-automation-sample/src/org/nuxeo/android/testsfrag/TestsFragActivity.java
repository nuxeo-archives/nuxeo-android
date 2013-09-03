package org.nuxeo.android.testsfrag;

import org.nuxeo.android.automationsample.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

public class TestsFragActivity extends FragmentActivity {

	Fragment firstFragment = null, secondFragment = null, temp = null;
	boolean firstShown = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tests_frag);
	}
	
	public void Connect(View v) {
		firstShown = true;
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if (firstFragment == null) {
			firstFragment = new ConnnectSampleFragment(); 
		} else {
			transaction.detach(firstFragment);
			secondFragment = firstFragment;
			firstFragment = new ConnnectSampleFragment();
		}
		transaction.replace(R.id.fragment_container, firstFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void Fetch(View v) {
		firstShown = false;
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if (firstFragment == null) {
			firstFragment = new SimpleFetchSampleFragment(); 
		} else {
			transaction.detach(firstFragment);
			secondFragment = firstFragment;
			firstFragment = new SimpleFetchSampleFragment();
		}
		transaction.replace(R.id.fragment_container, firstFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void HideLast(View v) {
		if (firstFragment == null) return;
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.detach(firstFragment);
		if (secondFragment != null) {
			transaction.attach(secondFragment);
		}
		transaction.commit();
	}
	
	public void SwitchFrag(View v) {
		if (firstFragment == null) return;

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if (secondFragment == null) {
			transaction.detach(firstFragment);
			secondFragment = firstFragment;
			if (firstShown == true) {
				firstFragment = new SimpleFetchSampleFragment();
				firstShown = false;
			} else {
				firstFragment = new ConnnectSampleFragment();
				firstShown = true;
			}
			transaction.replace(R.id.fragment_container, firstFragment);
		}
		else {
			transaction.detach(firstFragment);
			transaction.attach(secondFragment);
			temp = firstFragment;
			firstFragment = secondFragment;
			secondFragment = temp;
		}
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
