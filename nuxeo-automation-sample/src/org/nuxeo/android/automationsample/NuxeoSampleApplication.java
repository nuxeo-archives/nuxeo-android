package org.nuxeo.android.automationsample;

import org.nuxeo.android.context.NuxeoContext;
import org.nuxeo.android.context.NuxeoContextProvider;

import android.app.Application;

public class NuxeoSampleApplication extends Application implements
		NuxeoContextProvider {

	protected NuxeoContext nuxeoContext=null;

	@Override
	public NuxeoContext getNuxeoContext() {
		if (nuxeoContext==null) {
			nuxeoContext = new NuxeoContext();
		}
		return nuxeoContext;
	}

}
