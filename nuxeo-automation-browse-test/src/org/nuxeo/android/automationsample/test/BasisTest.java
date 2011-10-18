package org.nuxeo.android.automationsample.test;

import java.util.ArrayList;

import org.nuxeo.android.automationsample.HomeSampleActivity;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

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

}
