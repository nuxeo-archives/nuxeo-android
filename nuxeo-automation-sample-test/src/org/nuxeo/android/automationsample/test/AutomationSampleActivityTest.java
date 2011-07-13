package org.nuxeo.android.automationsample.test;

import org.nuxeo.android.automationsample.AutomationSampleActivity;
import org.nuxeo.android.automationsample.R;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.Spinner;

public class AutomationSampleActivityTest extends
		ActivityInstrumentationTestCase2<AutomationSampleActivity> {

	protected Button connectBtn;
	protected Spinner spinner;

//	public AutomationSampleActivityTest(Class<AutomationSampleActivity> activityClass) {
//		super(activityClass);
//	}

	public AutomationSampleActivityTest() {
		super("org.nuxeo.android.automationsample", AutomationSampleActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();

		AutomationSampleActivity mainActivity = getActivity();

		connectBtn = (Button) mainActivity.findViewById(R.id.connect);
		spinner = (Spinner) mainActivity.findViewById(R.id.opList);

	}

	public void testConnect() throws Throwable {

		assertTrue(spinner.getCount()==0);

		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				connectBtn.performClick();
			}
		});

		// wait for spinner to be displayed
		while (spinner.getVisibility()>0) {
			Thread.sleep(500);
		}

		assertTrue(spinner.getCount()>0);
	}
}
