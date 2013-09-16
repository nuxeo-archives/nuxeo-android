package org.nuxeo.android.appraisal.fragments;

import org.nuxeo.android.appraisal.R;
import org.nuxeo.android.fragments.BaseDocumentLayoutFragment;
import org.nuxeo.android.layout.LayoutMode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class LayoutFragment extends BaseDocumentLayoutFragment {

	protected Button saveBtn;
	
	public LayoutFragment() {
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.createeditlayout, container, false);

		saveBtn = (Button) v.findViewById(R.id.updateDocument);
		saveBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveDocument();
			}
		});
		return v;
	}

	@Override
	public void onResume() {
        super.onResume();
        if (getMode() == LayoutMode.VIEW) {
            saveBtn.setVisibility(View.GONE);
        } else {
            saveBtn.setVisibility(View.VISIBLE);
        }
    }

	@Override
	protected ViewGroup getLayoutContainer() {
		return (ViewGroup) getView().findViewById(R.id.layoutContainer);
	}

	@Override
	public BaseDocumentLayoutFragment getDocumentLayoutFragment() {
		if (documentLayoutFragment == null) {
			documentLayoutFragment = new LayoutFragment();
		}
		return documentLayoutFragment;
	}

}
