package org.nuxeo.android.automationsample;

import org.nuxeo.android.activities.BaseNuxeoActivity;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Simple base class for sharing code between samples and hiding as much as
 * possible all the UI code that is not related to Nuxeo SDK
 *
 * @author tiry
 *
 */
public abstract class BaseSampleListActivity extends BaseNuxeoActivity
		implements View.OnClickListener {

	protected ListView listView;
	protected TextView waitingMessage;
	protected Button refreshBtn;

	protected static final int EDIT_DOCUMENT = 0;
	protected static final int CREATE_DOCUMENT = 1;

	protected static final int CTXMNU_VIEW_DOCUMENT = 0;
	protected static final int CTXMNU_EDIT_DOCUMENT = 1;
	protected static final int CTXMNU_VIEW_ATTACHEMENT = 2;

	public BaseSampleListActivity() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nxcp);

		waitingMessage = (TextView) findViewById(R.id.waitingMessage);

		refreshBtn = (Button) findViewById(R.id.refreshBtn);
		refreshBtn.setOnClickListener(this);

		listView = (ListView) findViewById(R.id.myList);
		registerForContextMenu(listView);

		// trigger the data retrieval
		runAsyncDataRetrieval();
	}

	protected void onNuxeoDataRetrievalStarted() {
		waitingMessage.setText("Loading data ...");
		waitingMessage.setVisibility(View.VISIBLE);
		refreshBtn.setGravity(Gravity.RIGHT);
		refreshBtn.setEnabled(false);
	}

	@Override
	protected void onNuxeoDataRetrieved(Object data) {
		waitingMessage.setVisibility(View.INVISIBLE);
		refreshBtn.setEnabled(true);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == listView.getId()) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle("Menu for entry" + info.position);
			menu.add(Menu.NONE, CTXMNU_VIEW_DOCUMENT, 0, "View");
			menu.add(Menu.NONE, CTXMNU_EDIT_DOCUMENT, 1, "Edit");
			menu.add(Menu.NONE, CTXMNU_VIEW_ATTACHEMENT, 2, "View attachement");
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.listmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onClick(View arg0) {
		doRefresh();
	}

	protected abstract void doRefresh();

	// Activity menu handling
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemNew:
			Document newDoc = createNewDocument();
			startActivityForResult(
					new Intent(this, DocumentLayoutActivity.class).putExtra(
							DocumentLayoutActivity.DOCUMENT, newDoc).putExtra(
							DocumentLayoutActivity.MODE, LayoutMode.CREATE),
					CREATE_DOCUMENT);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	protected abstract Document createNewDocument();

	protected abstract void onDocumentCreate(Document newDocument);

	protected abstract void onDocumentUpdate(Document editedDocument);

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == EDIT_DOCUMENT && resultCode == RESULT_OK) {
			if (data.hasExtra(DocumentLayoutActivity.DOCUMENT)) {
				Document editedDocument = (Document) data.getExtras().get(
						DocumentLayoutActivity.DOCUMENT);
				onDocumentUpdate(editedDocument);
			}
		} else if (requestCode == CREATE_DOCUMENT && resultCode == RESULT_OK) {
			if (data.hasExtra(DocumentLayoutActivity.DOCUMENT)) {
				Document newDocument = (Document) data.getExtras().get(
						DocumentLayoutActivity.DOCUMENT);
				onDocumentCreate(newDocument);
			}
		}
	}

	protected abstract Document getContextMenuDocument(int selectedPosition);

	// Content menu handling
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int selectedPosition = info.position;
		Document doc = getContextMenuDocument(selectedPosition);

		if (item.getItemId() == CTXMNU_VIEW_DOCUMENT) {
			startActivity(new Intent(this, DocumentLayoutActivity.class)
			.putExtra(DocumentLayoutActivity.DOCUMENT, doc).putExtra(
					DocumentLayoutActivity.MODE, LayoutMode.VIEW));
			return true;
		} else if (item.getItemId() == CTXMNU_EDIT_DOCUMENT) {
			startActivityForResult(new Intent(this, DocumentLayoutActivity.class)
					.putExtra(DocumentLayoutActivity.DOCUMENT, doc).putExtra(
							DocumentLayoutActivity.MODE, LayoutMode.EDIT),
					EDIT_DOCUMENT);
			return true;
		} else if (item.getItemId() == CTXMNU_VIEW_ATTACHEMENT) {
			Uri blobUri = doc.getBlob();
			if (blobUri == null) {
				Toast.makeText(this,
		                "No Attachement available ",
		                Toast.LENGTH_SHORT).show();
			} else {
				startViewerFromBlob(blobUri);
			}
			return true;
		} else {
			return super.onContextItemSelected(item);
		}
	}

	@Override
	protected boolean requireAsyncDataRetrieval() {
		return true;
	}

}