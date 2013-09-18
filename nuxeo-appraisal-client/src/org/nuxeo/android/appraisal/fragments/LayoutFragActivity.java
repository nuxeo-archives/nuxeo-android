package org.nuxeo.android.appraisal.fragments;

import org.nuxeo.android.appraisal.R;
import org.nuxeo.android.fragments.BaseDocLayoutFragAct;
import org.nuxeo.android.fragments.BaseDocumentLayoutFragment;

public class LayoutFragActivity extends BaseDocLayoutFragAct {

	LayoutFragment test;
	
	@Override
	protected BaseDocumentLayoutFragment createDocumentLayoutFrag() {
		return new LayoutFragment();
	}

	@Override
	protected int getActivityLayout() {
		return R.layout.activity_layout_frag;
	}

	@Override
	public int getLayoutFragmentContainerId() {
		return R.id.edit_frag_container;
	}

}
