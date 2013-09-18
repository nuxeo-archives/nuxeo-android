package org.nuxeo.android.fragments;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

public abstract class BaseListFragmentActivity extends FragmentActivity implements
		BaseDocumentsListFragment.Callback, BaseDocumentLayoutFragment.Callback {

	protected boolean mTwoPane = false;

	public BaseListFragmentActivity() {
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (findViewById(getLayoutFragmentContainerId()) != null) {
			mTwoPane = true;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	FragmentManager fragManager = getSupportFragmentManager();
	    	FragmentTransaction backTransaction = fragManager.beginTransaction();
	    	Fragment frag = fragManager.findFragmentById(getLayoutFragmentContainerId());
	    	if(frag != null) {
	    		backTransaction.detach(frag);
	    		backTransaction.commit();
	    	}
	    }
	    return super.onKeyDown(keyCode, event);
	}
}
