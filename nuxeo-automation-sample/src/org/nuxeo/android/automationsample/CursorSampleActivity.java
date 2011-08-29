package org.nuxeo.android.automationsample;

import org.nuxeo.android.contentprovider.NuxeoDocumentCursor;
import org.nuxeo.android.context.NuxeoContext;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class CursorSampleActivity extends Activity implements
		View.OnClickListener {

	protected Button cpBtn;

	protected ListView listView;

	protected NuxeoDocumentCursor documentCursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nxcp);

		cpBtn = (Button) findViewById(R.id.cpbutton);
		cpBtn.setOnClickListener(this);

		listView = (ListView) findViewById(R.id.myList);
		registerForContextMenu(listView);
	}

	@Override
	public void onClick(View view) {
		if (view == cpBtn) {
			final Activity activity = this;
			// run in a separated thread to avoid freezing the UI in case of
			// network lag
			Runnable initTask = new Runnable() {
				@Override
				public void run() {

					try {
						Documents docs = NuxeoContext.get(getApplicationContext()).getDocumentManager().query("select * from Document", null, null, null, 0, 5, CacheBehavior.STORE);
						documentCursor = docs.asCursor();
						final String[] columns = new String[] { "_ID", "dc:title" };
	  	                final int[] to = new int[] { R.id.id_entry, R.id.title_entry };
						// wait for UI thread to do the display
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(activity, R.layout.list_item, documentCursor, columns, to);
								listView.setAdapter(mAdapter);
							}
						});
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			new Thread(initTask).start();
		}


	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId()==listView.getId()) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			menu.setHeaderTitle("Menu for entry" + info.position);
			menu.add(Menu.NONE, 0, 0, "View");
			menu.add(Menu.NONE, 1, 1, "Edit");
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	static final int EDIT_DOCUMENT = 0;

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int selectedPosition = info.position;
		Document doc = documentCursor.getDocument(selectedPosition);

		if (item.getItemId()==0) {
			// VIEW
			Log.i(CursorSampleActivity.class.getSimpleName(), "View on doc " + doc.getId() + "-" + doc.getTitle());
			return true;
		} else if (item.getItemId()==1) {
			Log.i(CursorSampleActivity.class.getSimpleName(), "Edit on doc " + doc.getId() + "-" + doc.getTitle());
			startActivityForResult(new Intent(this, CreateEditActivity.class).putExtra(CreateEditActivity.DOCUMENT, doc).putExtra(CreateEditActivity.MODE, CreateEditActivity.EDIT), EDIT_DOCUMENT);
			return true;
		} else {
			return super.onContextItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode==EDIT_DOCUMENT) {
			if (data.hasExtra(CreateEditActivity.DOCUMENT)) {
				Document editedDocument = (Document) data.getExtras().get(CreateEditActivity.DOCUMENT);
				documentCursor.documentChanged(editedDocument);
			}
		}
	}

}
