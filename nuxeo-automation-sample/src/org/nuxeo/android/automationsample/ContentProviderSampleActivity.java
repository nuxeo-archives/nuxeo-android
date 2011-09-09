package org.nuxeo.android.automationsample;

import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.database.Cursor;
import android.net.Uri;
import android.widget.SimpleCursorAdapter;

public class ContentProviderSampleActivity extends BaseSampleListActivity {

	protected Cursor cursor;

	// Executed on the background thread to avoid freezing the UI
	@Override
	protected Object retrieveNuxeoData() throws Exception {
		cursor = managedQuery(Uri.parse("content://nuxeo/documents"), null, null, null, null);
		return true;
	}

	// Called on the UIThread when Nuxeo data has been retrieved
	@Override
	protected void onNuxeoDataRetrieved(Object data) {
		super.onNuxeoDataRetrieved(data);
		final String[] columns = new String[] { "_ID", "dc:title", "status", "iconUri" };
		final int[] to = new int[] { R.id.id_entry, R.id.title_entry, R.id.status_entry, R.id.iconView };
		SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this,
				R.layout.list_item, cursor, columns, to);
		listView.setAdapter(mAdapter);
	}

	protected Document getContextMenuDocument(int selectedPosition) {
		//return documentCursor.getDocument(selectedPosition);
		return null;
	}

	protected Document createNewDocument() {
		return null;
	}

	protected void onDocumentCreate(Document newDocument) {
	}

	protected void onDocumentUpdate(Document editedDocument) {
	}

	protected void doRefresh() {
		//documentCursor.getDocumentsList().refreshAll();
	}
}
