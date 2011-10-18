package org.nuxeo.android.automationsample.test;

public class BrowseOfflineTest extends BaseBrowsingTest  {

	public void testOfflineCreate() throws Exception {

		goOffline();
		// do offline creation
		browseAndCreate("offline");

		goOnline();
		flushPending();

		browseAndCheck("offline");
	}

	public void testOfflineEdit() throws Exception {

		goOffline();

		// do offline creation
		browseAndEdit("offline");

		goOnline();
		flushPending();

		browseAndCheck("offline");

	}

}
