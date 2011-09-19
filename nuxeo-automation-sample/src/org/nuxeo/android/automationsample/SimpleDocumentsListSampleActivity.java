package org.nuxeo.android.automationsample;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.android.adapters.AbstractDocumentListAdapter;
import org.nuxeo.android.adapters.DocumentsListAdapter;
import org.nuxeo.android.documentprovider.DocumentProvider;
import org.nuxeo.android.documentprovider.LazyDocumentsList;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

public class SimpleDocumentsListSampleActivity extends BaseSampleListActivity {

	protected LazyUpdatableDocumentsList documentsList;

	// Executed on the background thread to avoid freezing the UI
	@Override
	protected Object retrieveNuxeoData() throws Exception {
		return getNuxeoContext().getDocumentManager().query(
				"select * from Document", null, null, null, 0, 10,
				CacheBehavior.STORE);
	}

	protected LazyUpdatableDocumentsList getDocumentList(Object data) {
		Documents docs = (Documents) data;
		return docs.asUpdatableDocumentsList();
	}

	// Called on the UIThread when Nuxeo data has been retrieved
	@Override
	protected void onNuxeoDataRetrieved(Object data) {
		super.onNuxeoDataRetrieved(data);
		// get the DocumentList from the async call result
		documentsList = getDocumentList(data);
		Map<Integer, String> mapping = new HashMap<Integer,String>();
		mapping.put(R.id.title_entry, "dc:title");
		mapping.put(R.id.status_entry, "status");
		mapping.put(R.id.iconView, "iconUri");
		mapping.put(R.id.description, "dc:description");
		mapping.put(R.id.id_entry, "uuid");
		AbstractDocumentListAdapter adapter = new DocumentsListAdapter(this, documentsList, R.layout.list_item, mapping, R.layout.list_item_loading);
		listView.setAdapter(adapter);
	}



	protected Document getContextMenuDocument(int selectedPosition) {
		return documentsList.getDocument(selectedPosition);
	}

	protected Document createNewDocument() {
		return new Document("/default-domain/workspaces/WS1","newAndroidDoc","File");
	}

	protected void onDocumentCreate(Document newDocument) {
		documentsList.createDocument(newDocument);
	}

	protected void onDocumentUpdate(Document editedDocument) {
		documentsList.updateDocument(editedDocument);
	}

	protected void doRefresh() {
		documentsList.refreshAll();
	}

	@Override
	protected LazyDocumentsList getDocumentsList() {
		return documentsList;
	}

}
