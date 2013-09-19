package org.nuxeo.android.fragments;

import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.os.Bundle;
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
		extras.putInt(BaseDocumentLayoutFragment.FRAGMENT_CONTAINER_ID, getLayoutFragmentContainerId());
		
		
		documentLayoutFrag = getDocumentLayoutFrag();
    	documentLayoutFrag.setArguments(extras);
		FragmentTransaction contentTransaction = getSupportFragmentManager().beginTransaction();
    	
		contentTransaction.replace(getLayoutFragmentContainerId(), documentLayoutFrag);
		contentTransaction.commit();
	}

	@Override
	public int getListFragmentContainerId() {
		// This activity should be used only on small-screen devices,
		// No listContainer here
		return 0;
	}

	@Override
	public boolean isTwoPane() {
		// This activity should be used only on small-screen devices,
		// Only one pane, here
		return false;
	}
}
