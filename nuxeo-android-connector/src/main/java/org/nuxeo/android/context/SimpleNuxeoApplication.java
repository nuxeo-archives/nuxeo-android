package org.nuxeo.android.context;

import android.app.Application;

public class SimpleNuxeoApplication extends Application implements NuxeoContextProvider {

	protected NuxeoContext nuxeoContext=null;

	public NuxeoContext getNuxeoContext() {
		if (nuxeoContext==null) {
			nuxeoContext = new NuxeoContext(this);
		}
		return nuxeoContext;
	}
}
