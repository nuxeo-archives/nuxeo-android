package org.nuxeo.android.automationsample;

import org.nuxeo.android.activities.BaseDocumentLayoutActivity;
import org.nuxeo.android.layout.LayoutMode;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class DocumentLayoutActivity extends BaseDocumentLayoutActivity implements View.OnClickListener {

	protected TextView title;

	protected Button saveBtn;
	protected Button cancelBtn;

	@Override
	protected ViewGroup getLayoutContainer() {
		return (ScrollView) findViewById(R.id.layoutContainer);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.createeditlayout);
		title = (TextView) findViewById(R.id.currentDocTitle);
		title.setText(getCurrentDocument().getTitle());

		saveBtn = (Button) findViewById(R.id.updateDocument);
		saveBtn.setOnClickListener(this);

		cancelBtn = (Button) findViewById(R.id.cancelDocument);
		cancelBtn.setOnClickListener(this);

		buildLayout();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (getMode()==LayoutMode.VIEW) {
			saveBtn.setVisibility(View.INVISIBLE);
		} else {
			saveBtn.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View view) {
		if (view == saveBtn) {
			saveDocument();
		} else if (view == cancelBtn) {
			cancelUpdate();
		}
	}

}
