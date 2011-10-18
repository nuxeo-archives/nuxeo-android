package org.nuxeo.android.automationsample.test;

import java.util.ArrayList;

import org.nuxeo.android.automationsample.HomeSampleActivity;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ListView;

import com.jayway.android.robotium.solo.Solo;

public class BrowseTest extends
		ActivityInstrumentationTestCase2<HomeSampleActivity> {

	private static final int ACTIVITY_WAIT_MILLIS = 500;
	private Solo solo;

	public BrowseTest() {
		super("org.nuxeo.android.automationsample", HomeSampleActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}

	public void testRecorded() throws Exception {

		// Start online
		getActivity().setSettings("http://10.213.3.241:8080/nuxeo/", "Administrator", "Administrator");
		getActivity().setOffline(false);
		Thread.sleep(500);

		// check online creation
		browseAndCreate("online");
		// check online edit
		browseAndEdit("online");

		// Go offline
		getActivity().setOffline(true);
		Thread.sleep(500);

		// do offline creation
		browseAndCreate("offline");

		// do offline edit
		browseAndEdit("offline");

	}

	protected void browseAndCreate(String tag) {
		solo.waitForActivity(
				"org.nuxeo.android.automationsample.HomeSampleActivity",
				ACTIVITY_WAIT_MILLIS);

		solo.clickOnView(findViewById(org.nuxeo.android.automationsample.R.id.browsetBtn));

		solo.waitForActivity(
				"org.nuxeo.android.automationsample.GetChildrenSampleActivity",
				ACTIVITY_WAIT_MILLIS);

		// Domain
		solo.clickInList(1);
		solo.waitForActivity(
				"org.nuxeo.android.automationsample.GetChildrenSampleActivity",
				ACTIVITY_WAIT_MILLIS);

		// Workspaces
		solo.clickInList(3);
		solo.waitForActivity(
				"org.nuxeo.android.automationsample.GetChildrenSampleActivity",
				ACTIVITY_WAIT_MILLIS);

		ListView listview = solo.getCurrentListViews().get(0);
		assertNotNull(listview);
		int count = listview.getAdapter().getCount();

		// new item
		solo.sendKey(Solo.MENU);
		solo.clickOnText("New item");
		solo.clickInList(3);

		solo.waitForActivity(
				"org.nuxeo.android.automationsample.DocumentLayoutActivity",
				ACTIVITY_WAIT_MILLIS);
		solo.clearEditText(0);
		solo.enterText(0, "Test Folder " + tag);
		solo.clickOnView(findViewById(org.nuxeo.android.automationsample.R.id.updateDocument));
		solo.waitForActivity(
				"org.nuxeo.android.automationsample.GetChildrenSampleActivity",
				ACTIVITY_WAIT_MILLIS);

		listview = solo.getCurrentListViews().get(0);
		assertNotNull(listview);
		int count2 = listview.getAdapter().getCount();
		assertEquals(count+1, count2);

		solo.goBack();
		solo.goBack();
		solo.goBack();

	}

	protected void browseAndEdit(String tag) {
		solo.waitForActivity(
				"org.nuxeo.android.automationsample.HomeSampleActivity",
				ACTIVITY_WAIT_MILLIS);

		solo.clickOnView(findViewById(org.nuxeo.android.automationsample.R.id.browsetBtn));

		solo.waitForActivity(
				"org.nuxeo.android.automationsample.GetChildrenSampleActivity",
				ACTIVITY_WAIT_MILLIS);

		// Domain
		solo.clickInList(1);
		solo.waitForActivity(
				"org.nuxeo.android.automationsample.GetChildrenSampleActivity",
				ACTIVITY_WAIT_MILLIS);

		// Workspaces
		solo.clickInList(3);
		solo.waitForActivity(
				"org.nuxeo.android.automationsample.GetChildrenSampleActivity",
				ACTIVITY_WAIT_MILLIS);

		solo.clickLongInList(0);
		solo.clickOnText("Edit");

		solo.waitForActivity(
				"org.nuxeo.android.automationsample.DocumentLayoutActivity",
				ACTIVITY_WAIT_MILLIS);
		solo.clearEditText(0);
		solo.enterText(0, "Test Folder " + tag + " Edited");
		solo.clickOnView(findViewById(org.nuxeo.android.automationsample.R.id.updateDocument));
		solo.waitForActivity(
				"org.nuxeo.android.automationsample.GetChildrenSampleActivity",
				ACTIVITY_WAIT_MILLIS);

		solo.goBack();
		solo.goBack();
		solo.goBack();

	}

	/**
	 * Enhanced view finder. First tries to find it from Activity, then from all Views under ViewRoot.
	 */
	public View findViewById(int id) {
		View view = solo.getView(id);
		if (view != null)
			return view;

		ArrayList<View> views = solo.getViews();
		for (View v : views) {
			if (v.getId() == id) {
				return v;
			}
		}
		return null;
	}

}
