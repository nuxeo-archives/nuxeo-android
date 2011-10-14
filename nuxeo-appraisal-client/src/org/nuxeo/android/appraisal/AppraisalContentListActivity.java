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

import android.view.Menu;
import android.view.SubMenu;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class AppraisalContentListActivity extends BaseDocumentsListActivity {

	public static final String ROOT_UUID_PARAM = "rootDocId";

	protected Document root;

	protected String getRootUUID() {
		if (getIntent().getExtras()!=null) {
			return getIntent().getExtras().getString(ROOT_UUID_PARAM);
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
				"select * from Document where ecm:mixinType != \"HiddenInNavigation\" AND ecm:isCheckedInVersion = 0 AND ecm:parentId=? order by dc:modified desc", new String[]{getRootUUID()}, null, null, 0, 10,
				CacheBehavior.STORE);
		if (docs!=null) {
			return docs.asUpdatableDocumentsList();
		}
		throw new RuntimeException("fetch Operation did return null");
	}

	@Override
	protected Class<? extends BaseDocumentLayoutActivity> getEditActivityClass() {
		return null;
	}

	@Override
	protected Document initNewDocument(String type) {
		return null;
	}


	@Override
	protected void setupViews() {
		setContentView(R.layout.nxcp);
		waitingMessage = (TextView) findViewById(R.id.waitingMessage);
		refreshBtn = (Button) findViewById(R.id.refreshBtn);
		listView = (ListView) findViewById(R.id.myList);
	}

	protected static final int MNU_NEW_PICTURE = 10;
	
	protected void populateMenu(Menu menu) {
		SubMenu subMenu = menu.addSubMenu(Menu.NONE, MNU_NEW_LISTITEM, 0, "New item");
		LinkedHashMap<String, String> types = getDocTypesForCreation();
		int idx = 1;
		for (String key : types.keySet()) {
			subMenu.add(Menu.NONE,MNU_NEW_LISTITEM+ idx, idx, types.get(key));
			idx++;
		}
		menu.add(Menu.NONE, MNU_VIEW_LIST_EXTERNAL, 1, "External View");
		menu.add(Menu.NONE, MNU_REFRESH, 2, "Refresh");
	}

}
