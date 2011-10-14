package org.nuxeo.android.appraisal;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.nuxeo.android.activities.BaseDocumentLayoutActivity;
import org.nuxeo.android.activities.BaseDocumentsListActivity;
import org.nuxeo.android.adapters.AbstractDocumentListAdapter;
import org.nuxeo.android.adapters.DocumentAttributeResolver;
import org.nuxeo.android.adapters.DocumentsListAdapter;
import org.nuxeo.android.documentprovider.LazyDocumentsList;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class AppraisalContentListActivity extends BaseDocumentsListActivity {

	public static final String ROOT_DOC_PARAM = "rootDoc";

	//protected Document root;

	protected Document getRoot() {
		if (getIntent().getExtras()!=null) {
			return (Document) getIntent().getExtras().get(ROOT_DOC_PARAM);
		}
		return null;
	}

	protected Map<Integer, String> getMapping() {
		Map<Integer, String> mapping = new HashMap<Integer, String>();
		mapping.put(R.id.title_entry, "dc:title");
		mapping.put(R.id.thumb, DocumentAttributeResolver.PICTUREURI+":Medium");
		return mapping;
	}

	@Override
	protected void displayDocumentList(ListView listView,
			LazyDocumentsList documentsList) {
		AbstractDocumentListAdapter adapter = new DocumentsListAdapter(this,
				documentsList, R.layout.picture_item, getMapping(),
				R.layout.list_item_loading);
		listView.setAdapter(adapter);
	}

	@Override
	protected LazyUpdatableDocumentsList fetchDocumentsList() throws Exception {
		Documents docs = (Documents) getNuxeoContext().getDocumentManager().query(
				"select * from Document where ecm:mixinType != \"HiddenInNavigation\" AND ecm:isCheckedInVersion = 0 AND ecm:parentId=? order by dc:modified desc", new String[]{getRoot().getId()}, null, null, 0, 10,
				CacheBehavior.STORE);
		if (docs!=null) {
			return docs.asUpdatableDocumentsList();
		}
		throw new RuntimeException("fetch Operation did return null");
	}

	@Override
	protected Class<? extends BaseDocumentLayoutActivity> getEditActivityClass() {
		return AppraisalLayoutActivity.class;
	}

	@Override
	protected Document initNewDocument(String type) {
		return new Document(getRoot().getPath(),"appraisalPicture-" + documentsList.getCurrentSize(),type);
	}

	@Override
	protected void onDocumentCreate(Document newDocument) {
		super.onDocumentCreate(newDocument);
		doRefresh();
	}

	@Override
	protected void setupViews() {
		setContentView(R.layout.nxcp);
		waitingMessage = (TextView) findViewById(R.id.waitingMessage);
		refreshBtn = (Button) findViewById(R.id.refreshBtn);
		listView = (ListView) findViewById(R.id.myList);
	}

	protected LinkedHashMap<String, String> getDocTypesForCreation() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Picture", "Picture");
		return map;
	}


}
