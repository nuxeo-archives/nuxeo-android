package org.nuxeo.android.automationsample.test;

import android.view.View;

import com.jayway.android.robotium.solo.Solo;

public class StartActivityForResultTest
    extends BasisTest
{

  public void testAttachFile() throws Exception
  {
	waitForNuxeoActivity("org.nuxeo.android.automationsample.HomeSampleActivity");
    solo.clickOnView(findViewById(org.nuxeo.android.automationsample.R.id.browsetBtn));
    waitForNuxeoActivity("org.nuxeo.android.automationsample.GetChildrenSampleActivity");
    solo.clickInList(1);
    waitForNuxeoActivity("org.nuxeo.android.automationsample.GetChildrenSampleActivity");
    solo.clickInList(3);
    waitForNuxeoActivity("org.nuxeo.android.automationsample.GetChildrenSampleActivity");
    solo.sendKey(Solo.MENU);
    solo.clickOnText("New item");
    solo.clickInList(1);
    waitForNuxeoActivity("org.nuxeo.android.automationsample.DocumentLayoutActivity");
    solo.clickOnScreen(111f, 230f);
    solo.clearEditText(0);
    solo.enterText(0, "Title");
    final View fileButton = findViewByTag("file:file:content");
    assertNotNull("Could not find the file button", fileButton);
    solo.clickOnView(fileButton);
  }

}
