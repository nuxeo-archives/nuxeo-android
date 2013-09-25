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

package org.nuxeo.android.fragments.test;

import org.nuxeo.android.activities.ListFragActivity;

import com.jayway.android.robotium.solo.Solo;

public abstract class BaseBrowsingTest extends BasisTest {
    
    protected boolean isTwoPane = false;

    protected String browseAndCreate(boolean online) throws Exception {
        String tag = online ? "online" : "offline";

        assertTrue(waitForNuxeoActivity("org.nuxeo.android.activities.HomeSampleActivity"));

        solo.clickOnView(findViewById(org.nuxeo.android.automationsample.R.id.browse_frag));

        assertTrue(waitForNuxeoActivity("org.nuxeo.android.activities.ListFragActivity"));

        // Domain
        solo.clickInList(1);
        assertTrue(waitForNuxeoActivity("org.nuxeo.android.activities.ListFragActivity"));

        // Workspaces
        solo.clickInList(3);
        assertTrue(waitForNuxeoActivity("org.nuxeo.android.activities.ListFragActivity"));

        isTwoPane = ((ListFragActivity) solo.getCurrentActivity()).isTwoPane();
        
        // new item
        if (!isTwoPane) {
            solo.sendKey(Solo.MENU);
        }
        solo.clickOnText("New item");
        solo.clickInList(3);
        String title = null;
        
        if (!isTwoPane) {
            assertTrue(waitForNuxeoActivity("org.nuxeo.android.activities.DocumentLayoutFragActivity"));
        } else {
            assertTrue(waitForNuxeoActivity("org.nuxeo.android.activities.ListFragActivity"));
        }
        title = "Folder " + tag + " " + System.currentTimeMillis();
        solo.clearEditText(0);
        solo.enterText(0, title);
        solo.clickOnView(findViewById(org.nuxeo.android.automationsample.R.id.updateDocument));

        assertTrue(waitForNuxeoActivity("org.nuxeo.android.activities.ListFragActivity"));


        if (!isTwoPane) {
            solo.sendKey(Solo.MENU);
        }
        solo.clickOnText("Refresh");
        
        waitForDocumentTitle(0, title);

        if (online) {
            waitForDocumentStatus(0, "");
        } else {
//            assertEquals("", getDocumentStatus(0));
        }

        solo.goBack();
        solo.goBack();
        solo.goBack();

        return title;
    }

    protected String browseAndEdit(boolean online) throws Exception {
        
        String tag = online ? "online" : "offline";

        assertTrue(waitForNuxeoActivity("org.nuxeo.android.activities.HomeSampleActivity"));

        solo.clickOnView(findViewById(org.nuxeo.android.automationsample.R.id.browse_frag));

        assertTrue(waitForNuxeoActivity("org.nuxeo.android.activities.ListFragActivity"));

        // Domain
        solo.clickInList(1);
        assertTrue(waitForNuxeoActivity("org.nuxeo.android.activities.ListFragActivity"));

        // Workspaces
        solo.clickInList(3);
        assertTrue(waitForNuxeoActivity("org.nuxeo.android.activities.ListFragActivity"));

        solo.clickLongInList(0);
        Thread.sleep(200);
        solo.clickOnText("Edit");

        if (!isTwoPane) {
            assertTrue(waitForNuxeoActivity("org.nuxeo.android.activities.DocumentLayoutFragActivity"));
        } else {
            assertTrue(waitForNuxeoActivity("org.nuxeo.android.activities.ListFragActivity"));
        }
        
        solo.clearEditText(0);
        String title = "Folder " + tag + " Edited" + System.currentTimeMillis();
        solo.enterText(0, title);
        solo.clickOnView(findViewById(org.nuxeo.android.automationsample.R.id.updateDocument));
        assertTrue(waitForNuxeoActivity("org.nuxeo.android.activities.ListFragActivity"));

        // refresh
        if (!isTwoPane) {
            solo.sendKey(Solo.MENU);
        }
        solo.clickOnText("Refresh");
        
//        assertTrue("failed to get expected title: '" + title + "'",
//                waitForDocumentTitle(0, title));

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
    
    protected void deleteCreatedFolder() throws Exception {
        assertTrue(waitForNuxeoActivity("org.nuxeo.android.activities.HomeSampleActivity"));

        solo.clickOnView(findViewById(org.nuxeo.android.automationsample.R.id.simple_list_frag));

        assertTrue(waitForNuxeoActivity("org.nuxeo.android.activities.ListFragActivity"));

        if (!isTwoPane) {
            solo.sendKey(Solo.MENU);
        }
        solo.clickOnText("Refresh");
        Thread.sleep(200);
        solo.clickLongInList(0);
        Thread.sleep(200);
        solo.clickOnText("Delete");
        Thread.sleep(200);
        
        solo.goBack();
    }

    protected void browseAndCheck(String title) throws Exception {
        assertTrue(waitForNuxeoActivity("org.nuxeo.android.activities.HomeSampleActivity"));

        solo.clickOnView(findViewById(org.nuxeo.android.automationsample.R.id.browse_frag));

        assertTrue(waitForNuxeoActivity("org.nuxeo.android.activities.ListFragActivity"));

        // Domain
        solo.clickInList(1);
        assertTrue(waitForNuxeoActivity("org.nuxeo.android.activities.ListFragActivity"));

        // Workspaces
        solo.clickInList(3);
        assertTrue(waitForNuxeoActivity("org.nuxeo.android.activities.ListFragActivity"));

        waitForDocumentTitle(0, title);
        waitForDocumentStatus(0, "");

        assertNotNull(getDocumentCreationDate(0));

        solo.clickLongInList(0);
        Thread.sleep(200);
        solo.clickOnText("View");

        if (!isTwoPane) {
            assertTrue(waitForNuxeoActivity("org.nuxeo.android.activities.DocumentLayoutFragActivity"));
        } else {
            assertTrue(waitForNuxeoActivity("org.nuxeo.android.activities.ListFragActivity"));
        }
        
        solo.goBack();
        solo.goBack();
        solo.goBack();
        
        if(!isTwoPane) {
            solo.goBack();
        }
    }

}
