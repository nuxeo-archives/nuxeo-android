package org.nuxeo.android.appraisal.fragments;

import org.nuxeo.android.appraisal.AppraisalListActivity;
import org.nuxeo.android.appraisal.R;
import org.nuxeo.android.fragments.BaseDocLayoutFragAct;
import org.nuxeo.android.fragments.BaseDocumentLayoutFragment;
import org.nuxeo.android.fragments.BaseDocumentsListFragment;
import org.nuxeo.android.fragments.BaseListFragmentActivity;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentTransaction;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class MainActivity extends BaseListFragmentActivity implements
		AppraisalListFragment.Callback {
	
	public static String FIRST_CALL = "first_call";
	
	protected final int SHOW_ACTIVITIES = 1010101;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_frag);

		if(savedInstanceState != null) {
			return;
		}
		FragmentTransaction listTransaction = getSupportFragmentManager()
				.beginTransaction();
		listFragment = new AppraisalListFragment();
		Bundle extras = getIntent().getExtras(); 
		if (extras !=null) {
			if(extras.getBoolean(FIRST_CALL) == false) {
				listFragment = new AppraisalContentListFragment();
			}
		}
		listTransaction.replace(R.id.list_frag_container, listFragment);
		listTransaction.commit();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(false);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			menu.add(Menu.NONE, SHOW_ACTIVITIES, 3, "Show activities").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		} else {
			menu.add(Menu.NONE, SHOW_ACTIVITIES, 3, "Show activities");
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
//			NavUtils.navigateUpFromSameTask(this);
			return true;
		case SHOW_ACTIVITIES :
			startActivity(new Intent(this, AppraisalListActivity.class));
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public int getLayoutFragmentContainerId() {
		return R.id.content_frag_container;
	}
	
	@Override
	public int getListFragmentContainerId() {
		return R.id.list_frag_container;
	}

	@Override
	public BaseDocumentLayoutFragment getLayoutFragment() {
		return new LayoutFragment();
	}

	@Override
	public Class<? extends BaseDocLayoutFragAct> getLayoutFragmentActivity() {
		return LayoutFragActivity.class;
	}
	
	@Override
	public void saveDocument(Document doc) {
		listFragment = (BaseDocumentsListFragment) getSupportFragmentManager().findFragmentById(R.id.list_frag_container);
		super.saveDocument(doc);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CANCELED) {
			((AppraisalListFragment)getSupportFragmentManager().findFragmentById(R.id.list_frag_container)).doRefresh();
		} else super.onActivityResult(requestCode, resultCode, data);
	}
}
