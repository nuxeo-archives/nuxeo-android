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

import android.view.View;

import com.jayway.android.robotium.solo.Solo;

public abstract class StartActivityForResultTest extends BasisTest {

    public void XXXtestAttachFile() throws Exception {
        waitForNuxeoActivity("org.nuxeo.android.automationsample.HomeSampleActivity");
        solo.clickOnView(findViewById(org.nuxeo.android.automationsample.R.id.browse));
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
        solo.enterText(0, "Title " + System.currentTimeMillis());
        final View fileButton = findViewByTag("file:file:content");
        assertNotNull("Could not find the file button", fileButton);
        final View titleEditText = findViewByTag("edittext:dc:title");
        assertNotNull("Could not find the title EditText control",
                titleEditText);
        hideSoftKeyboard(titleEditText);
        solo.clickOnView(fileButton);
        solo.waitForText("File upload completed");
    }

}
