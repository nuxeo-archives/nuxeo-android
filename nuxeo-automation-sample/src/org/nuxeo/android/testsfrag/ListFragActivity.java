package org.nuxeo.android.testsfrag;

import org.nuxeo.android.activities.BaseDocumentLayoutActivity;
import org.nuxeo.android.automationsample.DocumentLayoutActivity;
import org.nuxeo.android.automationsample.R;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;
import org.nuxeo.android.fragments.BaseDocLayoutFragAct;
import org.nuxeo.android.fragments.BaseDocumentsListFragment;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;

public class ListFragActivity extends FragmentActivity implements BaseSampleDocumentsListFragment.Callback {

	public static final int SIMPLE_LIST = 0;
	public static final int BROWSE_LIST = 1;
	
	private boolean mTwoPane = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_frag);
		
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list, menu);
		return true;
	}

	@Override
	public void onItemSelected(LazyUpdatableDocumentsList documentsList, int id) {

    	Document doc = documentsList.getDocument(id);
		if (mTwoPane) {
			DocumentLayoutFragment documentLayoutFrag = new DocumentLayoutFragment();
			FragmentTransaction contentTransaction1 = getSupportFragmentManager().beginTransaction();
			
			Bundle args = new Bundle();
			args.putSerializable(BaseDocumentLayoutActivity.DOCUMENT, doc);
        	args.putSerializable(BaseDocumentLayoutActivity.MODE, LayoutMode.VIEW);
        	args.putBoolean(BaseDocumentLayoutActivity.FIRST_CALL, true);
        	documentLayoutFrag.setArguments(args);
        	
			contentTransaction1.replace(R.id.content_frag_container, documentLayoutFrag);
			contentTransaction1.commit();
        } else {
            Intent intent = new Intent(new Intent(getBaseContext(), DocumentLayoutFragActivity.class)
            	.putExtra(BaseDocumentLayoutActivity.DOCUMENT, doc)
            	.putExtra(BaseDocumentLayoutActivity.MODE, LayoutMode.VIEW)
            	.putExtra(BaseDocumentLayoutActivity.FIRST_CALL, true));
            startActivityForResult(intent, BaseDocumentsListFragment.ACTION_EDIT_DOCUMENT);
        }
	}

}
