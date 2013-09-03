package org.nuxeo.android.testsfrag;

import org.nuxeo.android.automationsample.R;
import org.nuxeo.android.fragments.BaseDocLayoutFragAct;
import org.nuxeo.android.fragments.BaseDocumentLayoutFragment;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

public class DocumentLayoutFragActivity extends BaseDocLayoutFragAct implements DocumentLayoutFragment.Callback {

	@Override
	protected BaseDocumentLayoutFragment createDocumentLayoutFrag() {
		return new DocumentLayoutFragment();
	}

	protected int getActivityLayout() {
		return R.layout.activity_document_layout_frag;
	}

	@Override
	protected int getFragmentContainerId() {
		return R.id.edit_frag_container;
	}

	//TODO : implement this so that editFragment isn't deleted when allready created
//	@Override
//	public void exchangeFragments() {
//		FragmentTransaction contentTransaction = getSupportFragmentManager().beginTransaction();
//		contentTransaction.detach(secondFrag);
//		contentTransaction.attach(firstFragment);
//		contentTransaction.commit();
//		
//		temp = firstFragment;
//		firstFragment = secondFrag;
//		secondFrag = temp;
//	}

	@Override
	public void switchToEdit() {
		DocumentLayoutFragment documentLayoutFrag = new DocumentLayoutFragment();
		FragmentTransaction contentTransaction = getSupportFragmentManager().beginTransaction();
		
		Bundle args = new Bundle();
		args.putSerializable(BaseDocumentLayoutFragment.DOCUMENT, currentDocument);
    	args.putSerializable(BaseDocumentLayoutFragment.MODE, LayoutMode.EDIT);
    	args.putBoolean(BaseDocumentLayoutFragment.FIRST_CALL, false);
    	documentLayoutFrag.setArguments(args);
    	secondFrag = documentLayoutFrag;

    	contentTransaction.detach(firstFragment);
		contentTransaction.replace(getFragmentContainerId(), secondFrag);
//		contentTransaction.addToBackStack(null);
		contentTransaction.commit();
	}

	@Override
	public void switchToView() {
		DocumentLayoutFragment documentLayoutFrag = new DocumentLayoutFragment();
		FragmentTransaction contentTransaction = getSupportFragmentManager().beginTransaction();
		
		Bundle args = new Bundle();
		args.putSerializable(BaseDocumentLayoutFragment.DOCUMENT, currentDocument);
    	args.putSerializable(BaseDocumentLayoutFragment.MODE, LayoutMode.VIEW);
    	args.putBoolean(BaseDocumentLayoutFragment.FIRST_CALL, false);
    	documentLayoutFrag.setArguments(args);
    	secondFrag = documentLayoutFrag;
    	
    	contentTransaction.detach(firstFragment);
		contentTransaction.replace(getFragmentContainerId(), secondFrag);
//		contentTransaction.addToBackStack(null);
		contentTransaction.commit();
	}

}
