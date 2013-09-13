package org.nuxeo.android.testsfrag;

import org.nuxeo.android.activities.BaseDocumentLayoutActivity;
import org.nuxeo.android.automationsample.R;
import org.nuxeo.android.documentprovider.DocumentProviderSampleFragment;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;
import org.nuxeo.android.fragments.BaseDocLayoutFragAct;
import org.nuxeo.android.fragments.BaseDocumentLayoutFragment;
import org.nuxeo.android.fragments.BaseDocumentsListFragment;
import org.nuxeo.android.fragments.BaseListFragmentActivity;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

public class ListFragActivity extends BaseListFragmentActivity 
	implements BaseDocumentsListFragment.Callback, DocumentLayoutFragment.Callback {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_frag);

		FragmentTransaction listTransaction = getSupportFragmentManager().beginTransaction();
		Intent callingIntent = getIntent();
		if(callingIntent.getIntExtra("list", 0)==SIMPLE_LIST) {
			listFragment = new SimpleListFragment();
			listTransaction.replace(R.id.list_frag_container, listFragment);
			setTitle(R.string.title_simple_list);
		} else if(callingIntent.getIntExtra("list", 0) == DOCUMENT_PROVIDER) {
			listTransaction.replace(R.id.list_frag_container, new DocumentProviderSampleFragment());
			setTitle(R.string.title_doc_provider);
		} else if(callingIntent.getIntExtra("list", 0) == BROWSE_LIST) {
			listTransaction.replace(R.id.list_frag_container, new GetChildrenSampleFragment());
			setTitle(R.string.title_browse_repo);
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

	//TODO : implement this so that editFragment isn't deleted when allready created
//	@Override
//	public void exchangeFragments() {
//		// TODO Auto-generated method stub
//		
//	}
	
	@Override
	public int getLayoutFragmentContainerId() {
		return R.id.content_frag_container;
	}
	
	@Override
	public BaseDocumentLayoutFragment getLayoutFragment() {
		return new DocumentLayoutFragment();
	}

	@Override
	public Class<? extends BaseDocLayoutFragAct> getLayoutFragmentActivity() {
		return DocumentLayoutFragActivity.class;
	}
	
}
