package org.nuxeo.android.testsfrag;

import org.nuxeo.android.activities.BaseDocumentLayoutActivity;
import org.nuxeo.android.automationsample.R;
import org.nuxeo.android.documentprovider.DocumentProviderSampleFragment;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;
import org.nuxeo.android.fragments.BaseDocumentLayoutFragment;
import org.nuxeo.android.fragments.BaseDocumentsListFragment;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class ListFragActivity extends FragmentActivity 
	implements BaseDocumentsListFragment.Callback, DocumentLayoutFragment.Callback {

	public static final int SIMPLE_LIST = 0;
	public static final int BROWSE_LIST = 1;
	public static final int DOCUMENT_PROVIDER = 2;
	
	Document currentDocument = null;
	
	SimpleListFragment listFragment = null;

	private boolean mTwoPane = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_frag);

		FragmentTransaction listTransaction = getSupportFragmentManager().beginTransaction();
		Intent callingIntent = getIntent();
		if(callingIntent.getIntExtra("list", 0)==SIMPLE_LIST) {
			listFragment = new SimpleListFragment();
			listTransaction.replace(R.id.list_frag_container, listFragment);
		} else if(callingIntent.getIntExtra("list", 0) == DOCUMENT_PROVIDER) {
			listTransaction.replace(R.id.list_frag_container, new DocumentProviderSampleFragment());
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
		viewCurrentDoc();
	}
	
	@Override
	public void viewDocument(Document doc) {
		currentDocument = doc;
		viewCurrentDoc();
	}
	
	public void viewCurrentDoc() {
		if (mTwoPane) {
			DocumentLayoutFragment documentLayoutFrag = new DocumentLayoutFragment();
			FragmentTransaction contentTransaction1 = getSupportFragmentManager()
					.beginTransaction();

			Bundle args = new Bundle();
			args.putSerializable(DocumentLayoutFragment.DOCUMENT,
					currentDocument);
			args.putSerializable(DocumentLayoutFragment.MODE, LayoutMode.VIEW);
			args.putBoolean(DocumentLayoutFragment.FIRST_CALL, true);
			args.putInt(BaseDocumentLayoutFragment.FRAGMENT_CONTAINER_ID,
					R.id.content_frag_container);
			documentLayoutFrag.setArguments(args);

			contentTransaction1.replace(R.id.content_frag_container,
					documentLayoutFrag);
			contentTransaction1.commit();
		} else {
			Intent intent = new Intent(new Intent(getBaseContext(),
					DocumentLayoutFragActivity.class)
					.putExtra(DocumentLayoutFragment.DOCUMENT, currentDocument)
					.putExtra(DocumentLayoutFragment.MODE, LayoutMode.VIEW)
					.putExtra(DocumentLayoutFragment.FIRST_CALL, true));
			startActivityForResult(intent,
					BaseDocumentsListFragment.ACTION_EDIT_DOCUMENT);
		}
	}

	//TODO : implement this so that editFragment isn't deleted when allready created
//	@Override
//	public void exchangeFragments() {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == BaseDocumentsListFragment.ACTION_EDIT_DOCUMENT
				&& resultCode == RESULT_OK) {
			if (data.hasExtra(BaseDocumentLayoutActivity.DOCUMENT)) {
				Document editedDocument = (Document) data.getExtras().get(
						BaseDocumentLayoutActivity.DOCUMENT);
				saveDocument(editedDocument);
			}
//		} else if (requestCode == ACTION_CREATE_DOCUMENT
//				&& resultCode == RESULT_OK) {
//			if (data.hasExtra(BaseDocumentLayoutActivity.DOCUMENT)) {
//				Document newDocument = (Document) data.getExtras().get(
//						BaseDocumentLayoutActivity.DOCUMENT);
//				onDocumentCreate(newDocument);
//				doRefresh();
//			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void saveDocument(Document doc) {
		listFragment.onDocumentUpdate(doc);
		listFragment.doRefresh();
	}

	@Override
	public int getFragmentContainerId() {
		return R.id.content_frag_container;
	}

	@Override
	public void editDocument(Document doc) {

    	currentDocument = doc;
		if (mTwoPane) {
			DocumentLayoutFragment documentLayoutFrag = new DocumentLayoutFragment();
			FragmentTransaction contentTransaction1 = getSupportFragmentManager().beginTransaction();
			
			Bundle args = new Bundle();
			args.putSerializable(DocumentLayoutFragment.DOCUMENT, currentDocument);
        	args.putSerializable(DocumentLayoutFragment.MODE, LayoutMode.EDIT);
        	args.putBoolean(DocumentLayoutFragment.FIRST_CALL, true);
        	args.putInt(BaseDocumentLayoutFragment.FRAGMENT_CONTAINER_ID, R.id.content_frag_container);
        	documentLayoutFrag.setArguments(args);
        	
			contentTransaction1.replace(R.id.content_frag_container, documentLayoutFrag);
			contentTransaction1.commit();
        } else {
            Intent intent = new Intent(new Intent(getBaseContext(), DocumentLayoutFragActivity.class)
            	.putExtra(DocumentLayoutFragment.DOCUMENT, currentDocument)
            	.putExtra(DocumentLayoutFragment.MODE, LayoutMode.EDIT)
            	.putExtra(DocumentLayoutFragment.FIRST_CALL, true));
            startActivityForResult(intent, BaseDocumentsListFragment.ACTION_EDIT_DOCUMENT);
        }
	}
	
}
