package org.nuxeo.android.automationsample;

import org.nuxeo.android.activities.BaseNuxeoActivity;
import org.nuxeo.android.contentprovider.NuxeoDocumentContentProvider;
import org.nuxeo.android.cursor.NuxeoDocumentCursor;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.content.Intent;
import android.database.Cursor;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class CursorSampleActivity extends BaseNuxeoActivity implements
		View.OnClickListener {

	protected Button refreshBtn;

	protected ListView listView;

	protected NuxeoDocumentCursor documentCursor;

	protected TextView waitingMessage;

	protected static final int EDIT_DOCUMENT = 0;

	protected static final int CREATE_DOCUMENT = 1;

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

	// Called on the UIThread when Nuxeo data retrieval starts
	protected void onNuxeoDataRetrievalStarted() {
		waitingMessage.setText("Loading data ...");
		waitingMessage.setVisibility(View.VISIBLE);
		refreshBtn.setGravity(Gravity.RIGHT);
		refreshBtn.setEnabled(false);
	}

	// Executed on the background thread to avoid freezing the UI
	@Override
	protected Object retrieveNuxeoData() throws Exception {
		// Cursor cur = managedQuery(NuxeoDocumentContentProvider.CONTENT_URI,
		// null, null, null, null);
		return getNuxeoContext().getDocumentManager().query(
				"select * from Document", null, null, null, 0, 5,
				CacheBehavior.STORE);
	}

	// Called on the UIThread when Nuxeo data has been retrieved
	@Override
	protected void onNuxeoDataRetrieved(Object data) {
		waitingMessage.setVisibility(View.INVISIBLE);
		refreshBtn.setEnabled(true);

		Documents docs = (Documents) data;
		documentCursor = docs.asCursor();
		final String[] columns = new String[] { "_ID", "dc:title", "status" };
		final int[] to = new int[] { R.id.id_entry, R.id.title_entry,
				R.id.status_entry };
		SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this,
				R.layout.list_item, documentCursor, columns, to);
		listView.setAdapter(mAdapter);
	}

	// Context menu init
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == listView.getId()) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle("Menu for entry" + info.position);
			menu.add(Menu.NONE, 0, 0, "View");
			menu.add(Menu.NONE, 1, 1, "Edit");
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	// Content menu handling
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int selectedPosition = info.position;
		Document doc = documentCursor.getDocument(selectedPosition);

		if (item.getItemId() == 0) {
			// VIEW
			Log.i(CursorSampleActivity.class.getSimpleName(), "View on doc "
					+ doc.getId() + "-" + doc.getTitle());
			return true;
		} else if (item.getItemId() == 1) {
			Log.i(CursorSampleActivity.class.getSimpleName(), "Edit on doc "
					+ doc.getId() + "-" + doc.getTitle());
			startActivityForResult(new Intent(this, CreateEditActivity.class)
					.putExtra(CreateEditActivity.DOCUMENT, doc).putExtra(
							CreateEditActivity.MODE, CreateEditActivity.EDIT),
					EDIT_DOCUMENT);
			return true;
		} else {
			return super.onContextItemSelected(item);
		}
	}

	// Activity menu setup
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.listmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	// Activity menu handling
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemNew:
			Document newDoc = new Document("/default-domain/workspaces/WS1","newAndroidDoc","File");
			startActivityForResult(new Intent(this, CreateEditActivity.class)
			.putExtra(CreateEditActivity.DOCUMENT, newDoc).putExtra(
					CreateEditActivity.MODE, CreateEditActivity.CREATE),
			CREATE_DOCUMENT);
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == EDIT_DOCUMENT && resultCode == RESULT_OK) {
			if (data.hasExtra(CreateEditActivity.DOCUMENT)) {
				Document editedDocument = (Document) data.getExtras().get(
						CreateEditActivity.DOCUMENT);
				documentCursor.getDocumentsList()
						.updateDocument(editedDocument);
			}
		} else if (requestCode == CREATE_DOCUMENT && resultCode == RESULT_OK) {
			if (data.hasExtra(CreateEditActivity.DOCUMENT)) {
				Document newDocument = (Document) data.getExtras().get(
						CreateEditActivity.DOCUMENT);
				documentCursor.getDocumentsList().createDocument(newDocument);
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		documentCursor.getDocumentsList().refreshAll();
	}

}
