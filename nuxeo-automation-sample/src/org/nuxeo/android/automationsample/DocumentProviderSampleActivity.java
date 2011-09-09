package org.nuxeo.android.automationsample;

import org.nuxeo.android.cursor.NuxeoDocumentCursor;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.widget.SimpleCursorAdapter;

public class DocumentProviderSampleActivity extends BaseSampleListActivity {

	protected NuxeoDocumentCursor documentCursor;

	// Executed on the background thread to avoid freezing the UI
	@Override
	protected Object retrieveNuxeoData() throws Exception {
		return getNuxeoContext().getDocumentManager().query(
				"select * from Document", null, null, null, 0, 10,
				CacheBehavior.STORE);
	}

	// Called on the UIThread when Nuxeo data has been retrieved
	@Override
	protected void onNuxeoDataRetrieved(Object data) {
		super.onNuxeoDataRetrieved(data);

		Documents docs = (Documents) data;
		documentCursor = docs.asCursor();
		final String[] columns = new String[] { "_ID", "dc:title", "status", "iconUri" };
		final int[] to = new int[] { R.id.id_entry, R.id.title_entry, R.id.status_entry, R.id.iconView };
		SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this,
				R.layout.list_item, documentCursor, columns, to);
		listView.setAdapter(mAdapter);
	}

	protected Document getContextMenuDocument(int selectedPosition) {
		return documentCursor.getDocument(selectedPosition);
	}

	protected Document createNewDocument() {
		return new Document("/default-domain/workspaces/WS1","newAndroidDoc","File");
	}

	protected void onDocumentCreate(Document newDocument) {
		documentCursor.getUpdatableDocumentsList().createDocument(newDocument);
	}

	protected void onDocumentUpdate(Document editedDocument) {
		documentCursor.getUpdatableDocumentsList()
		.updateDocument(editedDocument);
	}

	protected void doRefresh() {
		documentCursor.getDocumentsList().refreshAll();
	}

}
