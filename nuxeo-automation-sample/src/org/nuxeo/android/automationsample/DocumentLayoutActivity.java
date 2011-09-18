package org.nuxeo.android.automationsample;

import org.nuxeo.android.activities.BaseNuxeoActivity;
import org.nuxeo.android.layout.LayoutDefinition;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.android.layout.NuxeoLayout;
import org.nuxeo.android.layout.StaticLayouts;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.IdRef;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class DocumentLayoutActivity extends BaseNuxeoActivity implements View.OnClickListener {

	public static final String DOCUMENT = "document";
	public static final String MODE = "mode";

	protected Document currentDocument;

	protected boolean requireAsyncFetch=true;

	protected LayoutMode getMode() {
		return (LayoutMode) getIntent().getExtras().get(MODE);
	}

	protected boolean isCreateMode() {
		return getMode()==LayoutMode.CREATE;
	}

	protected boolean isEditMode() {
		return getMode()==LayoutMode.EDIT;
	}

	protected Document getCurrentDocument() {
		if (currentDocument == null) {
			currentDocument = (Document) getIntent().getExtras().get(DOCUMENT);
		}
		return currentDocument;
	}

	protected TextView title;

	protected Button saveBtn;

	protected ScrollView layoutContainer;

	protected NuxeoLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.createeditlayout);
		title = (TextView) findViewById(R.id.currentDocTitle);
		title.setText(getCurrentDocument().getTitle());
		saveBtn = (Button) findViewById(R.id.updateDocument);
		saveBtn.setOnClickListener(this);

		layoutContainer = (ScrollView) findViewById(R.id.layoutContainer);

		LayoutDefinition layoutDef = LayoutDefinition.fromJSON(StaticLayouts.DEFAULT_LAYOUT);
		layout = layoutDef.buildLayout(this, getCurrentDocument(), layoutContainer, getMode());
	}

	@Override
	protected void onNuxeoDataRetrieved(Object data) {
		currentDocument = (Document) data;
		layout.refreshFromDocument(currentDocument);
        Toast.makeText(this,
                "Refreshed document",
                Toast.LENGTH_SHORT).show();
        requireAsyncFetch=false;
	}

	@Override
	protected Object retrieveNuxeoData() throws Exception {
		Document refreshedDocument = getNuxeoContext().getDocumentManager().getDocument(new IdRef(getCurrentDocument().getId()));
		return refreshedDocument;
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
	public void onClick(View arg0) {
		Document doc = getCurrentDocument();
		layout.applyChanges(doc);
		setResult(RESULT_OK, new Intent().putExtra(DOCUMENT, doc));
		this.finish();
	}

	@Override
	protected boolean requireAsyncDataRetrieval() {
		return requireAsyncFetch;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		layout.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

}
