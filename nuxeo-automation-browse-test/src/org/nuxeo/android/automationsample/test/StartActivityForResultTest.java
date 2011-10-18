package org.nuxeo.android.automationsample.test;

import android.view.View;

import com.jayway.android.robotium.solo.Solo;

public class StartActivityForResultTest
    extends BasisTest
{

  public void testAttachFile()
  {
    solo.waitForActivity("org.nuxeo.android.automationsample.HomeSampleActivity", ACTIVITY_WAIT_MILLIS);
    solo.clickOnView(findViewById(org.nuxeo.android.automationsample.R.id.browsetBtn));
    solo.waitForActivity("org.nuxeo.android.automationsample.GetChildrenSampleActivity", ACTIVITY_WAIT_MILLIS);
    solo.clickInList(1);
    solo.waitForActivity("org.nuxeo.android.automationsample.GetChildrenSampleActivity", ACTIVITY_WAIT_MILLIS);
    solo.clickInList(3);
    solo.waitForActivity("org.nuxeo.android.automationsample.GetChildrenSampleActivity", ACTIVITY_WAIT_MILLIS);
    solo.sendKey(Solo.MENU);
    solo.clickOnScreen(136f, 748f);
    solo.clickInList(1);
    solo.waitForActivity("org.nuxeo.android.automationsample.DocumentLayoutActivity", ACTIVITY_WAIT_MILLIS);
    solo.clickOnScreen(111f, 230f);
    solo.clearEditText(0);
    solo.enterText(0, "Title");
    // solo.clickOnScreen(97f, 558f);
    final View fileButton = solo.getCurrentActivity().getWindow().getDecorView().findViewWithTag("file:file:content");
    assertNotNull("Could not find the file button", fileButton);
    solo.clickOnView(fileButton);
  }

}
