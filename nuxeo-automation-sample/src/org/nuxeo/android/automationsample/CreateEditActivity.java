package org.nuxeo.android.automationsample;

import org.nuxeo.android.activities.BaseNuxeoActivity;
import org.nuxeo.android.layout.LayoutDefinition;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.StaticLayouts;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
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

	protected ScrollView layoutContainer;

	protected LayoutDefinition layoutDef;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.createeditlayout);
		title = (TextView) findViewById(R.id.currentDocTitle);
		title.setText(getCurrentDocument().getTitle());
		updateBtn = (Button) findViewById(R.id.updateDocument);
		updateBtn.setOnClickListener(this);

		layoutContainer = (ScrollView) findViewById(R.id.layoutContainer);

		layoutDef = LayoutDefinition.fromJSON(StaticLayouts.DEFAULT_LAYOUT);
		layoutDef.buildLayout(this, getCurrentDocument(), layoutContainer, LayoutMode.EDIT);
	}

	@Override
	public void onClick(View arg0) {
		Document doc = getCurrentDocument();
		layoutDef.apply(doc);
		setResult(RESULT_OK, new Intent().putExtra(DOCUMENT, doc));
		this.finish();
	}

	@Override
	protected boolean requireAsyncDataRetrieval() {
		return false;
	}

}
