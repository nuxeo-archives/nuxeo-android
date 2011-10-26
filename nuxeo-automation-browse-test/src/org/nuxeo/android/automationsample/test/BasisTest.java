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

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.nuxeo.android.automationsample.HomeSampleActivity;

import android.app.Activity;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.jayway.android.robotium.solo.Solo;

/**
 * Test annotations:
 * Small: No interaction with any file system or network.
 * Medium: Access to file systems on box which is running tests.
 * Large: Access to external file systems, networks, etc.
 *
 */
public abstract class BasisTest extends
        ActivityInstrumentationTestCase2<HomeSampleActivity> {

    protected static final int ACTIVITY_WAIT_MILLIS = 2000;

    protected static final int NUMBER_OF_TRIES = 20;

    protected Solo solo;

    public BasisTest() {
        super("org.nuxeo.android.automationsample", HomeSampleActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
        getActivity().setSettings("http://10.0.2.2:8080/nuxeo/",
                "Administrator", "Administrator");
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

    /**
     * Enhanced view finder. First tries to find it from Activity, then from all
     * Views under ViewRoot.
     */
    protected final View findViewById(int id) {
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

    protected void goOnline() throws Exception {
        getActivity().setOffline(false);
        Thread.sleep(500);
    }

    protected void goOffline() throws Exception {
        getActivity().setOffline(true);
        Thread.sleep(500);
    }

    protected void flushPending() throws Exception {
        getActivity().fushPending();
        Thread.sleep(500);
    }

    protected boolean waitForActivity(String activityName, long timeout) {
        long now = System.currentTimeMillis();
        long endTime;
        for (endTime = now + timeout; !solo.getCurrentActivity().getClass().getName().equals(
                activityName)
                && now < endTime; now = System.currentTimeMillis())
            ;
        return now < endTime;
    }

    protected boolean waitForNuxeoActivity(String activityName)
            throws Exception {
        Thread.sleep(300);
        boolean result = waitForActivity(activityName, ACTIVITY_WAIT_MILLIS);

        if (!result) {
            String currentActivityName = solo.getCurrentActivity().getClass().getName();
            throw new AssertionError("Unable to find activity " + activityName
                    + " ( current name is " + currentActivityName + ")");
        }

        Activity currentActivity = solo.getCurrentActivity();

        Method method = null;

        try {
            method = currentActivity.getClass().getMethod("isReady");
        } catch (NoSuchMethodException e) {

        }

        if (method == null) {
            if (!currentActivity.getClass().getSimpleName().equals(
                    "HomeSampleActivity")) {
                throw new RuntimeException("Unable to find isReady method");
            }
            return result;
        }

        boolean ready = (Boolean) method.invoke(currentActivity);
        int nbTry = NUMBER_OF_TRIES;
        while (!ready && nbTry > 0) {
            Thread.sleep(300);
            ready = (Boolean) method.invoke(currentActivity);
            nbTry -= 1;
        }
        return ready;
    }

    protected boolean waitForDocumentStatus(int position, String status)
            throws Exception {
        int nbTry = NUMBER_OF_TRIES;
        String actualStatus = getDocumentStatus(position);
        while (!(status.equals(actualStatus)) && nbTry > 0) {
            Thread.sleep(300);
            actualStatus = getDocumentStatus(position);
            nbTry -= 1;
        }
        return false;
    }

    protected String getDocumentStatus(int position) throws Exception {
        ArrayList<ListView> listViews = solo.getCurrentListViews();
        if (listViews == null || listViews.size() == 0) {
            return null;
        }
        ListView listview = listViews.get(0);
        ListAdapter adapter = listview.getAdapter();

        Method method = adapter.getClass().getMethod("getDocumentStatus",
                Integer.class);
        if (method != null) {
            return (String) method.invoke(adapter, position);
        } else {

            return null;
        }
    }

    protected Object getDocumentCreationDate(int position) throws Exception {
        ArrayList<ListView> listViews = solo.getCurrentListViews();
        if (listViews == null || listViews.size() == 0) {
            return null;
        }
        ListView listview = listViews.get(0);
        ListAdapter adapter = listview.getAdapter();

        Method method = null;

        try {
            method = adapter.getClass().getMethod("getDocumentAttribute",
                    Integer.class, String.class);
        } catch (NoSuchMethodException e) {
            Log.e(this.getClass().getSimpleName(),
                    "Unable to find test method getDocumentAttribute on adapter "
                            + adapter.getClass().getName());
        }

        if (method != null) {
            return method.invoke(adapter, position, "dc:created");
        } else {
            return null;
        }
    }

    protected String getDocumentTitle(int position) throws Exception {
        ArrayList<ListView> listViews = solo.getCurrentListViews();
        if (listViews == null || listViews.size() == 0) {
            return "No List View";
        }
        ListView listview = listViews.get(0);
        ListAdapter adapter = listview.getAdapter();

        Method method = null;

        try {
            method = adapter.getClass().getMethod("getDocumentAttribute",
                    Integer.class, String.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(
                    "Unable to find test method getDocumentAttribute on adapter "
                            + adapter.getClass().getName());
            // Log.e(this.getClass().getSimpleName(),
            // "Unable to find test method getDocumentAttribute on adapter " +
            // adapter.getClass().getName());
        }

        if (method != null) {
            String value = (String) method.invoke(adapter, position, "dc:title");
            if (value == null) {
                throw new RuntimeException("Null title returned");
            }
            return value;
        } else {
            return null;
        }
    }

    // return true if the the title expected is found
    protected boolean waitForDocumentTitle(int position, String expectedTitle)
            throws Exception {
        String title = getDocumentTitle(position);
        int nbTry = NUMBER_OF_TRIES;
        while (!(expectedTitle.equals(title)) && nbTry > 0) {
            Thread.sleep(300);
            title = getDocumentTitle(position);
            nbTry -= 1;
        }

        if (nbTry == 0) {
            String activityName = solo.getCurrentActivity().getClass().getSimpleName();
            throw new AssertionError("Unable to find title " + expectedTitle
                    + "; actual title is " + title + " on activity "
                    + activityName);
        }

        return nbTry > 0;
    }

    protected View findViewByTag(
    /* Class<? extends Activity> activityClass, */String tag) {
        // final List<View> views = solo.getViews();
        // for (View view : views)
        // {
        // if (tag.equals(view.getTag()) == true)
        // {
        // return view;
        // }
        // }
        // return null;
        // return
        // solo.getCurrentActivity().findViewById(android.R.id.content).findViewWithTag(tag);
        // final List<Activity> activities = solo.getAllOpenedActivities();
        // for (Activity activity : activities)
        // {
        // if (activityClass == activity.getClass())
        // {
        // return activity.getWindow().getDecorView().findViewWithTag(tag);
        // }
        // }
        return solo.getCurrentActivity().getWindow().getDecorView().findViewWithTag(
                tag);
    }

    /**
     * Sometimes, we need to hide the soft keyboard, because the virtual
     * keyboard process seems to intercept the touch event.
     *
     * <p>
     * See
     * http://code.google.com/p/robotium/issues/detail?can=1&q=133&colspec=ID
     * %20Type%20Stars%20Status%20Priority%20Milestone%20Owner%20Summary&id=133
     * for the discussion thread.
     * </p>
     *
     * @param editText
     */
    protected void hideSoftKeyboard(final View editText) {
        final InputMethodManager inputMethodManager = (InputMethodManager) solo.getCurrentActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * The name 'test preconditions' is a convention to signal that if this
     * test doesn't pass, the test case was not set up properly and it might
     * explain any and all failures in other tests. This is not guaranteed
     * to run before other tests, as JUnit uses reflection to find the tests.
     */
    @SmallTest
    public void testPreconditions() {
    }

}
