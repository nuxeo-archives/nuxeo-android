package org.nuxeo.android.automationsample;

import org.nuxeo.android.activities.BaseNuxeoActivity;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CreateEditActivity extends BaseNuxeoActivity implements View.OnClickListener {

	public static final String DOCUMENT = "document";
	public static final String MODE = "mode";
	public static final String CREATE = "create";
	public static final String EDIT = "edit";

	protected Document currentDocument;

	protected String getMode() {
		return getIntent().getExtras().getString(MODE);
	}

	protected boolean isCreateMode() {
		return CREATE.equals(getMode());
	}

	protected boolean isEditMode() {
		return EDIT.equals(getMode());
	}

	protected Document getCurrentDocument() {
		if (currentDocument == null) {
			currentDocument = (Document) getIntent().getExtras().get(DOCUMENT);
		}
		return currentDocument;
	}

	protected TextView title;

	protected Button updateBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.createeditlayout);
		title = (TextView) findViewById(R.id.currentDocTitle);
		title.setText(getCurrentDocument().getTitle());
		updateBtn = (Button) findViewById(R.id.updateDocument);
		updateBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		Document doc = getCurrentDocument();
		doc.set("dc:title", doc.getTitle() + "--Edited--");
		setResult(RESULT_OK, new Intent().putExtra(DOCUMENT, doc));
		this.finish();
	}

}
