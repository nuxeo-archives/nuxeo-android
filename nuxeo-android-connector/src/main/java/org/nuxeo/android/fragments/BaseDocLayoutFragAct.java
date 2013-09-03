package org.nuxeo.android.fragments;

import org.nuxeo.android.activities.BaseDocumentLayoutActivity;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Activity which should be used on small-screen devices only to load a
 * Class which extends @BaseDocumentLayoutFragment
 */
public abstract class BaseDocLayoutFragAct extends FragmentActivity {

	protected abstract BaseDocumentLayoutFragment createDocumentLayoutFrag();
	
	protected abstract int getActivityLayout();
	
	protected abstract int getFragmentContainerId();
	
	protected BaseDocumentLayoutFragment documentLayoutFrag;
		
	protected Document currentDocument;
	
	protected Fragment firstFragment = null, secondFrag = null, temp = null;
	
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
		currentDocument = (Document) extras.get(BaseDocumentLayoutActivity.DOCUMENT);
		LayoutMode mode = (LayoutMode) extras.get(BaseDocumentLayoutActivity.MODE);
		Boolean firstCall = extras.getBoolean(BaseDocumentLayoutActivity.FIRST_CALL);
		
		
		documentLayoutFrag = getDocumentLayoutFrag();
		FragmentTransaction contentTransaction = getSupportFragmentManager().beginTransaction();
		
		Bundle args = new Bundle();
		args.putSerializable(BaseDocumentLayoutFragment.DOCUMENT, currentDocument);
    	args.putSerializable(BaseDocumentLayoutFragment.MODE, mode);
    	args.putBoolean(BaseDocumentLayoutFragment.FIRST_CALL, firstCall);
    	documentLayoutFrag.setArguments(args);
    	firstFragment = documentLayoutFrag;
    	
		contentTransaction.replace(getFragmentContainerId(), documentLayoutFrag);
		contentTransaction.commit();
	}
	
}
