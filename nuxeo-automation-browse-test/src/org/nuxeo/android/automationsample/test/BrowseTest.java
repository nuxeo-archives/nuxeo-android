package org.nuxeo.android.automationsample.test;

import com.jayway.android.robotium.solo.Solo;

public class BrowseTest extends BaseBrowsingTest {

	public BrowseTest() {
		super();
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		getActivity().setSettings("http://10.213.3.241:8080/nuxeo/", "Administrator", "Administrator");
		Thread.sleep(500);
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

	public void testOnlineCreateEdit() throws Exception {
		goOnline();
		// check online creation
		browseAndCreate("online");
		// check online edit
		browseAndEdit("online");
	}

}
