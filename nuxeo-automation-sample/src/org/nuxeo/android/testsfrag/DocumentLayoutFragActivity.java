package org.nuxeo.android.testsfrag;

import org.nuxeo.android.automationsample.R;
import org.nuxeo.android.automationsample.R.layout;
import org.nuxeo.android.automationsample.R.menu;
import org.nuxeo.android.fragments.BaseDocLayoutFragAct;
import org.nuxeo.android.layout.NuxeoLayout;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;

public class DocumentLayoutFragActivity extends BaseDocLayoutFragAct {
    
	//TODO
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_document_layout_frag);
//		fragmentContainerId = R.id.edit_frag_container;
//
//		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//		transaction.replace(fragmentContainerId, new DocumentLayoutFragment());
//		transaction.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.document_layout, menu);
		return true;
	}

}
