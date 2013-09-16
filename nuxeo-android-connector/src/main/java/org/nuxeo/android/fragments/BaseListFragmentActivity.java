package org.nuxeo.android.fragments;

import org.nuxeo.android.activities.BaseDocumentLayoutActivity;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public abstract class BaseListFragmentActivity extends FragmentActivity implements
		BaseDocumentsListFragment.Callback, BaseDocumentLayoutFragment.Callback {

	public static final int SIMPLE_LIST = 0;
	public static final int BROWSE_LIST = 1;
	public static final int DOCUMENT_PROVIDER = 2;

	protected BaseDocumentLayoutFragment viewFrag = null, editFrag = null;

	protected Document currentDocument = null;

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
	public void saveDocument(Document doc) {
		listFragment = (BaseDocumentsListFragment) getSupportFragmentManager().findFragmentById(getListFragmentContainerId());
		listFragment.onDocumentUpdate(doc);
		listFragment.doRefresh();
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
			BaseDocumentLayoutFragment documentLayoutFrag = getLayoutFragment();
			FragmentTransaction contentTransaction1 = getSupportFragmentManager()
					.beginTransaction();

			Bundle args = new Bundle();
			args.putSerializable(BaseDocumentLayoutFragment.DOCUMENT,
					currentDocument);
			args.putSerializable(BaseDocumentLayoutFragment.MODE, LayoutMode.VIEW);
			args.putBoolean(BaseDocumentLayoutFragment.FIRST_CALL, true);
			args.putInt(BaseDocumentLayoutFragment.FRAGMENT_CONTAINER_ID,
					getLayoutFragmentContainerId());
			documentLayoutFrag.setArguments(args);

			contentTransaction1.replace(getLayoutFragmentContainerId(),
					documentLayoutFrag);
			contentTransaction1.commit();
		} else {
			Intent intent = new Intent(new Intent(getBaseContext(),
					getLayoutFragmentActivity())
					.putExtra(BaseDocumentLayoutFragment.DOCUMENT, currentDocument)
					.putExtra(BaseDocumentLayoutFragment.MODE, LayoutMode.VIEW)
					.putExtra(BaseDocumentLayoutFragment.FIRST_CALL, true));
			startActivityForResult(intent,
					BaseDocumentsListFragment.ACTION_EDIT_DOCUMENT);
		}
	}
	
	public abstract Class <? extends BaseDocLayoutFragAct> getLayoutFragmentActivity();

	@Override
	public void editDocument(Document doc) {

    	currentDocument = doc;
		if (mTwoPane) {
			BaseDocumentLayoutFragment documentLayoutFrag = getLayoutFragment();
			FragmentTransaction contentTransaction1 = getSupportFragmentManager().beginTransaction();
			
			Bundle args = new Bundle();
			args.putSerializable(BaseDocumentLayoutFragment.DOCUMENT, currentDocument);
        	args.putSerializable(BaseDocumentLayoutFragment.MODE, LayoutMode.EDIT);
        	args.putBoolean(BaseDocumentLayoutFragment.FIRST_CALL, true);
        	args.putInt(BaseDocumentLayoutFragment.FRAGMENT_CONTAINER_ID, getLayoutFragmentContainerId());
        	documentLayoutFrag.setArguments(args);
        	
			contentTransaction1.replace(getLayoutFragmentContainerId(), documentLayoutFrag);
			contentTransaction1.commit();
        } else {
            Intent intent = new Intent(new Intent(getBaseContext(), getLayoutFragmentActivity())
            	.putExtra(BaseDocumentLayoutFragment.DOCUMENT, currentDocument)
            	.putExtra(BaseDocumentLayoutFragment.MODE, LayoutMode.EDIT)
            	.putExtra(BaseDocumentLayoutFragment.FIRST_CALL, true));
            startActivityForResult(intent, BaseDocumentsListFragment.ACTION_EDIT_DOCUMENT);
        }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == BaseDocumentsListFragment.ACTION_EDIT_DOCUMENT
				&& resultCode == RESULT_OK) {
			if (data.hasExtra(BaseDocumentLayoutActivity.DOCUMENT)) {
				Document editedDocument = (Document) data.getExtras().get(
						BaseDocumentLayoutFragment.DOCUMENT);
				saveDocument(editedDocument);
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
