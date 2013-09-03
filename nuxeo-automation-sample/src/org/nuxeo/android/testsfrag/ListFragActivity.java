package org.nuxeo.android.testsfrag;

import org.nuxeo.android.automationsample.R;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;
import org.nuxeo.android.fragments.BaseDocumentLayoutFragment;
import org.nuxeo.android.fragments.BaseDocumentsListFragment;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class ListFragActivity extends FragmentActivity 
	implements BaseSampleDocumentsListFragment.Callback, DocumentLayoutFragment.Callback {

	public static final int SIMPLE_LIST = 0;
	public static final int BROWSE_LIST = 1;
	
	Document currentDocument = null;
	
	private boolean mTwoPane = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_frag);

		if(savedInstanceState != null) return;
		FragmentTransaction listTransaction = getSupportFragmentManager().beginTransaction();
		Intent callingIntent = getIntent();
		if(callingIntent.getIntExtra("list", 0)==SIMPLE_LIST) {
			listTransaction.replace(R.id.list_frag_container, new SimpleListFragment());	
		} else if(callingIntent.getIntExtra("list", 0) == BROWSE_LIST) {
			listTransaction.replace(R.id.list_frag_container, new GetChildrenSampleFragment());
		}
		listTransaction.commit();
		
		if (findViewById(R.id.content_frag_container) != null) {
			mTwoPane = true;
			//we can preload a document here :
//			FragmentTransaction contentTransaction = getSupportFragmentManager().beginTransaction();
//			contentTransaction.replace(R.id.content_frag_container, new SimpleFetchSampleFragment());
//			contentTransaction.commit();
		}
	}

	@Override
	public void viewDocument(LazyUpdatableDocumentsList documentsList, int id) {

    	currentDocument = documentsList.getDocument(id);
		if (mTwoPane) {
			DocumentLayoutFragment documentLayoutFrag = new DocumentLayoutFragment();
			FragmentTransaction contentTransaction1 = getSupportFragmentManager().beginTransaction();
			
			Bundle args = new Bundle();
			args.putSerializable(DocumentLayoutFragment.DOCUMENT, currentDocument);
        	args.putSerializable(DocumentLayoutFragment.MODE, LayoutMode.VIEW);
        	args.putBoolean(DocumentLayoutFragment.FIRST_CALL, true);
        	documentLayoutFrag.setArguments(args);
        	
			contentTransaction1.replace(R.id.content_frag_container, documentLayoutFrag);
			contentTransaction1.commit();
        } else {
            Intent intent = new Intent(new Intent(getBaseContext(), DocumentLayoutFragActivity.class)
            	.putExtra(DocumentLayoutFragment.DOCUMENT, currentDocument)
            	.putExtra(DocumentLayoutFragment.MODE, LayoutMode.VIEW)
            	.putExtra(DocumentLayoutFragment.FIRST_CALL, true));
            startActivityForResult(intent, BaseDocumentsListFragment.ACTION_EDIT_DOCUMENT);
        }
	}

	//TODO : implement this so that editFragment isn't deleted when allready created
//	@Override
//	public void exchangeFragments() {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void switchToView() {
		DocumentLayoutFragment documentLayoutFrag = new DocumentLayoutFragment();
		FragmentTransaction contentTransaction = getSupportFragmentManager().beginTransaction();
		
		Bundle args = new Bundle();
		args.putSerializable(BaseDocumentLayoutFragment.DOCUMENT, currentDocument);
    	args.putSerializable(BaseDocumentLayoutFragment.MODE, LayoutMode.VIEW);
    	args.putBoolean(BaseDocumentLayoutFragment.FIRST_CALL, false);
    	documentLayoutFrag.setArguments(args);
//    	secondFrag = documentLayoutFrag;
    	
//    	contentTransaction.detach(firstFragment);
		contentTransaction.replace(getFragmentContainerId(), documentLayoutFrag);
		contentTransaction.commit();
	}

	@Override
	public void switchToEdit() {
		DocumentLayoutFragment documentLayoutFrag = new DocumentLayoutFragment();
		FragmentTransaction contentTransaction = getSupportFragmentManager().beginTransaction();
		
		Bundle args = new Bundle();
		args.putSerializable(BaseDocumentLayoutFragment.DOCUMENT, currentDocument);
    	args.putSerializable(BaseDocumentLayoutFragment.MODE, LayoutMode.EDIT);
    	args.putBoolean(BaseDocumentLayoutFragment.FIRST_CALL, false);
    	documentLayoutFrag.setArguments(args);
//    	secondFrag = documentLayoutFrag;

//    	contentTransaction.detach(firstFragment);
		contentTransaction.replace(getFragmentContainerId(), documentLayoutFrag);
		contentTransaction.commit();
	}

	
	protected int getFragmentContainerId() {
		return R.id.content_frag_container;
	}
}
