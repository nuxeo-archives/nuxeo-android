package org.nuxeo.android.testsfrag;

import org.nuxeo.android.automationsample.R;
import org.nuxeo.android.fragments.BaseDocLayoutFragAct;
import org.nuxeo.android.fragments.BaseDocumentLayoutFragment;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

public class DocumentLayoutFragActivity extends BaseDocLayoutFragAct {

	@Override
	protected BaseDocumentLayoutFragment createDocumentLayoutFrag() {
		return new DocumentLayoutFragment();
	}

	protected int getActivityLayout() {
		return R.layout.activity_document_layout_frag;
	}

	@Override
	public void saveDocument(Document doc) {
        setResult(RESULT_OK, new Intent().putExtra(BaseDocumentLayoutFragment.DOCUMENT, doc));
        this.finish();
	}

	@Override
	public int getFragmentContainerId() {
		return R.id.edit_frag_container;
	}

}
