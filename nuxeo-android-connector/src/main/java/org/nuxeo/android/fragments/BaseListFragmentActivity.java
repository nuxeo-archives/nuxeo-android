package org.nuxeo.android.fragments;

import org.nuxeo.android.activities.BaseDocumentLayoutActivity;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

public abstract class BaseListFragmentActivity extends FragmentActivity implements
		BaseDocumentsListFragment.Callback, BaseDocumentLayoutFragment.Callback {

	protected BaseDocumentLayoutFragment viewFrag = null, editFrag = null;

	protected BaseDocumentsListFragment listFragment = null;

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
	
	public abstract int getLayoutFragmentContainerId();
	public abstract int getListFragmentContainerId();
	
	public abstract BaseDocumentLayoutFragment getLayoutFragment();

	@Override
	public void saveNewDocument(Document doc) {
		listFragment = (BaseDocumentsListFragment) getSupportFragmentManager()
				.findFragmentById(getListFragmentContainerId());
		listFragment.onDocumentCreate(doc);
		listFragment.doRefresh();
		if(mTwoPane){
			FragmentManager fragManager = getSupportFragmentManager();
			FragmentTransaction transaction = fragManager.beginTransaction();
			transaction.remove(fragManager.findFragmentById(getLayoutFragmentContainerId()));
			transaction.commit();
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

	public abstract Class <? extends BaseDocLayoutFragAct> getLayoutFragmentActivity();

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == BaseDocumentsListFragment.ACTION_CREATE_DOCUMENT
				&& resultCode == RESULT_OK) {
			if (data.hasExtra(BaseDocumentLayoutActivity.DOCUMENT)) {
				Document newDocument = (Document) data.getExtras().get(
						BaseDocumentLayoutActivity.DOCUMENT);
				saveNewDocument(newDocument);
			}
		} else if (mTwoPane) {
			BaseDocumentLayoutFragment currentContentFrag = (BaseDocumentLayoutFragment) getSupportFragmentManager()
					.findFragmentById(getLayoutFragmentContainerId());
			currentContentFrag.onActivityResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

//	@Override
//	public void exchangeFragments() {
//		FragmentManager fragManager = getSupportFragmentManager();
//		DocumentLayoutFragment fragment = (DocumentLayoutFragment) fragManager.findFragmentById(R.id.content_frag_container);
//		FragmentTransaction transaction = fragManager.beginTransaction();
//		if(fragment.getMode() == LayoutMode.VIEW) {
//			viewFrag = fragment;
//			transaction.detach(fragment);
//			if (editFrag == null) {
//				editFrag = new DocumentLayoutFragment();
//
//				Bundle args = new Bundle();
//				args.putSerializable(DocumentLayoutFragment.DOCUMENT,
//						currentDocument);
//				args.putSerializable(DocumentLayoutFragment.MODE, LayoutMode.EDIT);
//				args.putBoolean(DocumentLayoutFragment.FIRST_CALL, false);
//				args.putInt(BaseDocumentLayoutFragment.FRAGMENT_CONTAINER_ID,
//						R.id.content_frag_container);
//				editFrag.setArguments(args);
//				transaction.add(R.id.content_frag_container, editFrag);
//			} else {
//				transaction.attach(editFrag);
//			}
//		} else {
//			editFrag = fragment;
//			transaction.detach(fragment);
//			transaction.attach(viewFrag);
//		}
//		transaction.commit();
//	}
}
