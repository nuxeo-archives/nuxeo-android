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

package org.nuxeo.android.automationsample;

import junit.framework.Test;
import junit.framework.TestSuite;
import android.test.suitebuilder.TestSuiteBuilder;

/**
 * A test suite running all tests under current package.
 *
 * To run all suites found in this apk:
 * adb shell am instrument -w \
 * org.nuxeo.android.automationsample.test/android.test.InstrumentationTestRunner
 *
 * To run just this suite from the command line:
 * adb shell am instrument -w -e class org.nuxeo.android.automationsample.AllTests \
 * org.nuxeo.android.automationsample.test/android.test.InstrumentationTestRunner
 *
 * To run an individual test case:
 * adb shell am instrument -w \
 * -e class org.nuxeo.android.automationsample.test.BrowseTest \
 * org.nuxeo.android.automationsample.test/android.test.InstrumentationTestRunner
 *
 * To run an individual test:
 * adb shell am instrument -w \
 * -e class org.nuxeo.android.automationsample.test.StartActivityForResultTest#testAttachFile \
 * org.nuxeo.android.automationsample.test/android.test.InstrumentationTestRunner
 */
public class AllTests extends TestSuite {

    public static Test suite() {
        return new TestSuiteBuilder(AllTests.class).includeAllPackagesUnderHere().build();
    }
}
