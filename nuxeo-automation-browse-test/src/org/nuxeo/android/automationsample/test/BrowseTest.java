package org.nuxeo.android.automationsample.test;


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
		String title =browseAndCreate(false);

		goOnline();
		flushPending();
		browseAndCheck(title);
	}

	protected void doOfflineEdit() throws Exception {
		goOffline();
		// do offline creation
		String title =browseAndEdit(false);

		goOnline();
		flushPending();
		browseAndCheck(title);
	}



}
