package org.nuxeo.android.automationsample.test;

import android.widget.ListView;

import com.jayway.android.robotium.solo.Solo;

public class BaseBrowsingTest extends BasisTest{

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

	protected void browseAndEdit(String tag) throws Exception {
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
		Thread.sleep(200);
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

	protected void browseAndCheck(String tag) throws Exception {
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
		Thread.sleep(200);
		solo.clickOnText("View");

		solo.waitForActivity(
				"org.nuxeo.android.automationsample.DocumentLayoutActivity",
				ACTIVITY_WAIT_MILLIS);

		String title = solo.getText(0).getText().toString();

		solo.goBack();
		solo.goBack();
		solo.goBack();

	}

}
