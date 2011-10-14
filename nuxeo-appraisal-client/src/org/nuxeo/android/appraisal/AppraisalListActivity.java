package org.nuxeo.android.appraisal;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.android.activities.BaseDocumentLayoutActivity;
import org.nuxeo.android.activities.BaseDocumentsListActivity;
import org.nuxeo.android.adapters.AbstractDocumentListAdapter;
import org.nuxeo.android.adapters.DocumentsListAdapter;
import org.nuxeo.android.documentprovider.LazyDocumentsList;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;
import org.nuxeo.android.layout.LayoutMode;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.content.Intent;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class AppraisalListActivity extends BaseDocumentsListActivity {

	protected static final int MNU_CONFIG = 20;
	protected static final int MNU_NETWORK_CONFIG = 21;
	protected static final int MNU_SERVER_CONFIG = 22;
	protected static final int CTXMNU_VIEW_PICTURES = 10;

	@Override
	protected void displayDocumentList(ListView listView,
			LazyDocumentsList documentsList) {
		AbstractDocumentListAdapter adapter = new DocumentsListAdapter(this,
				documentsList, R.layout.list_item, getMapping(),
				R.layout.list_item_loading);
		listView.setAdapter(adapter);
	}

	protected Map<Integer, String> getMapping() {
		Map<Integer, String> mapping = new HashMap<Integer, String>();
		mapping.put(R.id.title_entry, "dc:title");
		mapping.put(R.id.status_entry, "status");
		mapping.put(R.id.client, "appraisal:customerName");
		mapping.put(R.id.declaration_date, "(date)dc:created");
		mapping.put(R.id.visite_date, "(date)appraisal:date_of_visit");
		return mapping;
	}

	@Override
	protected LazyUpdatableDocumentsList fetchDocumentsList() throws Exception {
		String user = getNuxeoContext().getSession().getLogin().getUsername();
		Documents docs = (Documents) getNuxeoContext()
				.getDocumentManager()
				.query(
						"select * from Appraisal where appraisal:assignee=? order by dc:modified desc",
						new String[]{user}, null, "common,dublincore,appraisal", 0, 10, CacheBehavior.STORE);
		if (docs != null) {
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setupViews() {
		setContentView(R.layout.nxcp);
		waitingMessage = (TextView) findViewById(R.id.waitingMessage);
		refreshBtn = (Button) findViewById(R.id.refreshBtn);
		listView = (ListView) findViewById(R.id.myList);
	}

	protected void populateMenu(Menu menu) {
		SubMenu subMenu = menu.addSubMenu(Menu.NONE, MNU_CONFIG, 0, "Config");
		subMenu.add(Menu.NONE,MNU_NETWORK_CONFIG, 0, "Network");
		subMenu.add(Menu.NONE,MNU_SERVER_CONFIG, 1, "Settings");
		menu.add(Menu.NONE, MNU_REFRESH, 2, "Refresh");
	}

	protected void populateContextMenu(Document doc, ContextMenu menu) {
		menu.add(Menu.NONE, CTXMNU_VIEW_DOCUMENT, 0, "View Appraisal");
		menu.add(Menu.NONE, CTXMNU_EDIT_DOCUMENT, 1, "Edit Appraisal");
		menu.add(Menu.NONE, CTXMNU_VIEW_PICTURES, 2, "View pictures");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MNU_NETWORK_CONFIG:
			startActivity(new Intent(getApplicationContext(), NetworkSettingsActivity.class));
			return true;
		case MNU_SERVER_CONFIG:
			startActivity(new Intent(getApplicationContext(), ServerSettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int selectedPosition = info.position;
		Document doc = getContextMenuDocument(selectedPosition);

		if (item.getItemId() == CTXMNU_VIEW_PICTURES) {
            startActivity(new Intent(this, AppraisalContentListActivity.class)
                    .putExtra(AppraisalContentListActivity.ROOT_UUID_PARAM, doc.getId()));
            return true;
		} else {
			return super.onContextItemSelected(item);
		}
	}

	@Override
	protected void onListItemClicked(int listItemPosition) {
		Document doc = documentsList.getDocument(listItemPosition);
        startActivity(new Intent(this, AppraisalContentListActivity.class)
        .putExtra(AppraisalContentListActivity.ROOT_UUID_PARAM, doc.getId()));
	}
}
