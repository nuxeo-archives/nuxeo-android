package org.nuxeo.android.appraisal.fragments;

import org.nuxeo.android.appraisal.AppraisalListActivity;
import org.nuxeo.android.appraisal.R;
import org.nuxeo.android.fragments.BaseDocLayoutFragAct;
import org.nuxeo.android.fragments.BaseDocumentLayoutFragment;
import org.nuxeo.android.fragments.BaseDocumentsListFragment;
import org.nuxeo.android.fragments.BaseListFragmentActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

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
		BaseDocumentsListFragment listFragment = null;
		listFragment = new AppraisalListFragment();
		listTransaction.replace(getListFragmentContainerId(), listFragment);
		listTransaction.commit();
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == AppraisalListFragment.REQUEST_SERVER) {
			((AppraisalListFragment)getSupportFragmentManager().findFragmentById(R.id.list_frag_container)).doRefresh();
		} else super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean isTwoPane() {
		return mTwoPane;
	}
}
