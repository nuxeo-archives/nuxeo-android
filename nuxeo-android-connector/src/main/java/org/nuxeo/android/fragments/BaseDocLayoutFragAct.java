package org.nuxeo.android.fragments;

import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

/**
 * Activity which should be used on small-screen devices only to load a
 * Class which extends @BaseDocumentLayoutFragment
 */
public abstract class BaseDocLayoutFragAct extends FragmentActivity  implements BaseDocumentLayoutFragment.Callback {

	protected abstract BaseDocumentLayoutFragment createDocumentLayoutFrag();
	
	protected abstract int getActivityLayout();
	
	public abstract int getLayoutFragmentContainerId();
	
	protected BaseDocumentLayoutFragment documentLayoutFrag;
		
	protected Document currentDocument;
	
	
	protected BaseDocumentLayoutFragment getDocumentLayoutFrag() {
		if (documentLayoutFrag==null) {
			documentLayoutFrag = createDocumentLayoutFrag();
		}
		return documentLayoutFrag;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(getActivityLayout());
		
		Bundle extras = getIntent().getExtras();
		currentDocument = (Document) extras.get(BaseDocumentLayoutFragment.DOCUMENT);
		LayoutMode mode = (LayoutMode) extras.get(BaseDocumentLayoutFragment.MODE);
		Boolean firstCall = extras.getBoolean(BaseDocumentLayoutFragment.FIRST_CALL);
		
		
		documentLayoutFrag = getDocumentLayoutFrag();
		FragmentTransaction contentTransaction = getSupportFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		args.putSerializable(BaseDocumentLayoutFragment.DOCUMENT, currentDocument);
    	args.putSerializable(BaseDocumentLayoutFragment.MODE, mode);
    	args.putBoolean(BaseDocumentLayoutFragment.FIRST_CALL, firstCall);
    	args.putInt(BaseDocumentLayoutFragment.FRAGMENT_CONTAINER_ID, getLayoutFragmentContainerId());
    	documentLayoutFrag.setArguments(args);
    	
		contentTransaction.replace(getLayoutFragmentContainerId(), documentLayoutFrag);
		contentTransaction.commit();
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//we have to call DocumentLayoutFrag().onActivityResult because activities in wrappers are called from this activity context

		BaseDocumentLayoutFragment currentContentFrag = (BaseDocumentLayoutFragment) getSupportFragmentManager()
				.findFragmentById(getLayoutFragmentContainerId());
		currentContentFrag.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

	@Override
	public int getListFragmentContainerId() {
		// No listContainer here
		return 0;
	}

	@Override
	public boolean isTwoPane() {
		return false;
	}
}
