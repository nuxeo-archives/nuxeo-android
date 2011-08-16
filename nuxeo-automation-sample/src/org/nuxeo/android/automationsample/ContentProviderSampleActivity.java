package org.nuxeo.android.automationsample;

import org.nuxeo.android.contentprovider.NuxeoDocumentContentProvider;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ContentProviderSampleActivity extends Activity implements
		View.OnClickListener {

	protected Button cpBtn;

	protected ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nxcp);

		cpBtn = (Button) findViewById(R.id.cpbutton);
		cpBtn.setOnClickListener(this);

		listView = (ListView) findViewById(R.id.myList);
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
					final Cursor cur = managedQuery(
							NuxeoDocumentContentProvider.CONTENT_URI,
							null, null, null, null);

					final String[] columns = new String[] { "_ID", "dc:title" };
  	                final int[] to = new int[] { R.id.id_entry, R.id.title_entry };


					// wait for UI thread to do the display
					runOnUiThread(new Runnable() {
						@Override
						public void run() {

							SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(activity, R.layout.list_item, cur, columns, to);
							listView.setAdapter(mAdapter);
						}
					});
				}
			};
			new Thread(initTask).start();

		}
	}
}
