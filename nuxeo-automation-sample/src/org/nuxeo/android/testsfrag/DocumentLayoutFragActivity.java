package org.nuxeo.android.testsfrag;

import org.nuxeo.android.activities.BaseDocumentLayoutActivity;
import org.nuxeo.android.automationsample.R;
import org.nuxeo.android.fragments.BaseDocLayoutFragAct;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;

public class DocumentLayoutFragActivity extends BaseDocLayoutFragAct {
    
	//TODO : update @BaseDocLayoutFragAct so that less work has to be done here
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_document_layout_frag);
		
		Bundle extras = getIntent().getExtras();
		Document currentDoc = (Document) extras.get(BaseDocumentLayoutActivity.DOCUMENT);
		LayoutMode mode = (LayoutMode) extras.get(BaseDocumentLayoutActivity.MODE);
		Boolean firstCall = extras.getBoolean(BaseDocumentLayoutActivity.FIRST_CALL);
		
		DocumentLayoutFragment documentLayoutFrag = new DocumentLayoutFragment();
		FragmentTransaction contentTransaction = getSupportFragmentManager().beginTransaction();
		
		Bundle args = new Bundle();
		args.putSerializable(BaseDocumentLayoutActivity.DOCUMENT, currentDoc);
    	args.putSerializable(BaseDocumentLayoutActivity.MODE, mode);
    	args.putBoolean(BaseDocumentLayoutActivity.FIRST_CALL, firstCall);
    	documentLayoutFrag.setArguments(args);
    	
		contentTransaction.replace(getFragmentContainerId(), documentLayoutFrag);
		contentTransaction.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.document_layout, menu);
		return true;
	}

	@Override
	protected int getFragmentContainerId() {
		return R.id.edit_frag_container;
	}

}
