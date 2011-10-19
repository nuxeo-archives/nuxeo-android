/*
 * (C) Copyright 2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 */

package org.nuxeo.android.automationsample.test;

import com.jayway.android.robotium.solo.Solo;

public class BaseBrowsingTest extends BasisTest {

    protected String browseAndCreate(boolean online) throws Exception {

        String tag = online ? "online" : "offline";

        waitForNuxeoActivity("org.nuxeo.android.automationsample.HomeSampleActivity");

        solo.clickOnView(findViewById(org.nuxeo.android.automationsample.R.id.browsetBtn));

        waitForNuxeoActivity("org.nuxeo.android.automationsample.GetChildrenSampleActivity");

        // Domain
        solo.clickInList(1);
        waitForNuxeoActivity("org.nuxeo.android.automationsample.GetChildrenSampleActivity");

        // Workspaces
        solo.clickInList(3);
        waitForNuxeoActivity("org.nuxeo.android.automationsample.GetChildrenSampleActivity");

        // new item
        solo.sendKey(Solo.MENU);
        solo.clickOnText("New item");
        solo.clickInList(3);

        waitForNuxeoActivity("org.nuxeo.android.automationsample.DocumentLayoutActivity");

        String title = "Folder " + tag + " " + System.currentTimeMillis();
        solo.clearEditText(0);
        solo.enterText(0, title);
        solo.clickOnView(findViewById(org.nuxeo.android.automationsample.R.id.updateDocument));
        waitForNuxeoActivity("org.nuxeo.android.automationsample.GetChildrenSampleActivity");

        String newTitle = getDocumentTitle(0);
        assertNotNull(newTitle);
        assertEquals(title, newTitle);

        if (online) {
            waitForDocumentStatus(0, "");
        } else {
            assertEquals("new", getDocumentStatus(0));
        }

        solo.goBack();
        solo.goBack();
        solo.goBack();

        return newTitle;
    }

    protected String browseAndEdit(boolean online) throws Exception {

        String tag = online ? "online" : "offline";

        waitForNuxeoActivity("org.nuxeo.android.automationsample.HomeSampleActivity");

        solo.clickOnView(findViewById(org.nuxeo.android.automationsample.R.id.browsetBtn));

        waitForNuxeoActivity("org.nuxeo.android.automationsample.GetChildrenSampleActivity");

        // Domain
        solo.clickInList(1);
        waitForNuxeoActivity("org.nuxeo.android.automationsample.GetChildrenSampleActivity");

        // Workspaces
        solo.clickInList(3);
        waitForNuxeoActivity("org.nuxeo.android.automationsample.GetChildrenSampleActivity");

        solo.clickLongInList(0);
        Thread.sleep(200);
        solo.clickOnText("Edit");

        waitForNuxeoActivity("org.nuxeo.android.automationsample.DocumentLayoutActivity");

        solo.clearEditText(0);
        String title = "Folder " + tag + " Edited" + System.currentTimeMillis();
        solo.enterText(0, title);
        solo.clickOnView(findViewById(org.nuxeo.android.automationsample.R.id.updateDocument));
        waitForNuxeoActivity("org.nuxeo.android.automationsample.GetChildrenSampleActivity");

		solo.clickOnView(findViewById(org.nuxeo.android.automationsample.R.id.refreshBtn));
		
		assertTrue("failed to get expected title: '" + title +"'", waitForDocumentTitle(0, title));

        if (online) {
            waitForDocumentStatus(0, "");
        } else {
            assertEquals("updated", getDocumentStatus(0));
        }

        solo.goBack();
        solo.goBack();
        solo.goBack();

		return title;
	}

    protected void browseAndCheck(String title) throws Exception {

        waitForNuxeoActivity("org.nuxeo.android.automationsample.HomeSampleActivity");

        solo.clickOnView(findViewById(org.nuxeo.android.automationsample.R.id.browsetBtn));

        waitForNuxeoActivity("org.nuxeo.android.automationsample.GetChildrenSampleActivity");

        // Domain
        solo.clickInList(1);
        waitForNuxeoActivity("org.nuxeo.android.automationsample.GetChildrenSampleActivity");

        // Workspaces
        solo.clickInList(3);
        waitForNuxeoActivity("org.nuxeo.android.automationsample.GetChildrenSampleActivity");

        String newTitle = getDocumentTitle(0);
        assertNotNull(newTitle);
        assertEquals(title, newTitle);
        waitForDocumentStatus(0, "");

        assertNotNull(getDocumentCreationDate(0));

        solo.clickLongInList(0);
        Thread.sleep(200);
        solo.clickOnText("View");

        waitForNuxeoActivity("org.nuxeo.android.automationsample.DocumentLayoutActivity");

        solo.goBack();
        solo.goBack();
        solo.goBack();
        solo.goBack();
    }

}
