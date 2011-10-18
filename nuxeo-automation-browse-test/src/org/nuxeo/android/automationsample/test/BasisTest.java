package org.nuxeo.android.automationsample.test;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.nuxeo.android.automationsample.HomeSampleActivity;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.jayway.android.robotium.solo.Solo;

public abstract class BasisTest
    extends ActivityInstrumentationTestCase2<HomeSampleActivity>
{

  protected static final int ACTIVITY_WAIT_MILLIS = 500;

  protected Solo solo;

  public BasisTest()
  {
    super("org.nuxeo.android.automationsample", HomeSampleActivity.class);
  }

  @Override
  public void setUp()
      throws Exception
  {
    solo = new Solo(getInstrumentation(), getActivity());
    getActivity().setSettings("http://192.168.56.1:8080/nuxeo/", "Administrator", "Administrator");
  }

  @Override
  public void tearDown()
      throws Exception
  {
    try
    {
      solo.finalize();
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }
    getActivity().finish();
    super.tearDown();
  }

  /**
   * Enhanced view finder. First tries to find it from Activity, then from all Views under ViewRoot.
   */
  protected final View findViewById(int id)
  {
    View view = solo.getView(id);
    if (view != null)
      return view;

    ArrayList<View> views = solo.getViews();
    for (View v : views)
    {
      if (v.getId() == id)
      {
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

	protected boolean waitForNuxeoActivity(String activityName) throws Exception {
		Thread.sleep(200);
		boolean result = solo.waitForActivity(
			"org.nuxeo.android.automationsample.GetChildrenSampleActivity",
			ACTIVITY_WAIT_MILLIS);

		if (!result) {
			return false;
		}
		Activity currentActivity = solo.getCurrentActivity();

		Method method = currentActivity.getClass().getMethod("isReady");

		if (method==null) {
			if (!currentActivity.getClass().getSimpleName().equals("HomeSampleActiivty")) {
				throw new RuntimeException("Unable to find isReady method");
			}
			return result;
		}

		boolean ready = (Boolean) method.invoke(currentActivity);
		int nbTry = 10;
		while (!ready && nbTry>0) {
			Thread.sleep(200);
			ready = (Boolean) method.invoke(currentActivity);
			nbTry-=1;
		}
		return ready;
	}

	protected boolean waitForDocumentStatus(int position, String status ) throws Exception {
		int nbTry = 10;
		String actualStatus = getDocumentStatus(position);
		while (!(status.equals(actualStatus)) && nbTry>0) {
			Thread.sleep(200);
			actualStatus = getDocumentStatus(position);
			nbTry-=1;
		}
		return false;
	}

	protected String getDocumentStatus(int position) throws Exception {
		ArrayList<ListView> listViews = solo.getCurrentListViews();
		if (listViews==null || listViews.size()==0) {
			return null;
		}
		ListView listview = listViews.get(0);
		ListAdapter adapter = listview.getAdapter();

		Method method = adapter.getClass().getMethod("getDocumentStatus", Integer.class);
		if (method!=null) {
			return  (String) method.invoke(adapter, position);
		} else {

			return null;
		}
	}

	protected Object getDocumentCreationDate(int position) throws Exception {
		ArrayList<ListView> listViews = solo.getCurrentListViews();
		if (listViews==null || listViews.size()==0) {
			return null;
		}
		ListView listview = listViews.get(0);
		ListAdapter adapter = listview.getAdapter();

		Method method = adapter.getClass().getMethod("getDocumentAttribute", Integer.class, String.class);
		if (method!=null) {
			return  method.invoke(adapter, position, "dc:created");
		} else {

			return null;
		}
	}

	protected String getDocumentTitle(int position) throws Exception {
		ArrayList<ListView> listViews = solo.getCurrentListViews();
		if (listViews==null || listViews.size()==0) {
			return null;
		}
		ListView listview = listViews.get(0);
		ListAdapter adapter = listview.getAdapter();

		Method method = adapter.getClass().getMethod("getDocumentAttribute", Integer.class, String.class);
		if (method!=null) {
			return  (String) method.invoke(adapter, position, "dc:title");
		} else {

			return null;
		}
	}
}
