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

import android.test.suitebuilder.annotation.LargeTest;

public class BrowseTest extends BaseBrowsingTest {

    public BrowseTest() {
        super();
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

    @LargeTest
    public void testCreateEdit() throws Exception {

        doOnlineTests();

        doOfflineCreate();

        doOfflineEdit();

    }

    protected void doOnlineTests() throws Exception {
        goOnline();
        // check online creation
        browseAndCreate(true);
        // check online edit
        browseAndEdit(true);
    }

    protected void doOfflineCreate() throws Exception {
        goOffline();
        // do offline creation
        String title = browseAndCreate(false);

        goOnline();
        flushPending();
        browseAndCheck(title);
    }

    protected void doOfflineEdit() throws Exception {
        goOffline();
        // do offline creation
        String title = browseAndEdit(false);

        goOnline();
        flushPending();
        browseAndCheck(title);
    }

}
