package org.nuxeo.android.context;

import android.content.Context;

public class NuxeoContextFactory {

	protected static NuxeoContext nuxeoContext = null;

	public static NuxeoContext getNuxeoContext(Context context) {
		if (nuxeoContext==null) {
			nuxeoContext = new NuxeoContext(context);
		}
		return nuxeoContext;
	}


}
