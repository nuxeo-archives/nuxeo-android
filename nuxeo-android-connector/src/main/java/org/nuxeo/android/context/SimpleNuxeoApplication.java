package org.nuxeo.android.context;

import org.nuxeo.android.cache.DefaultResponseCacheManager;
import org.nuxeo.android.pending.DefaultDeferedUpdateManager;

import android.app.Application;

public class SimpleNuxeoApplication extends Application implements NuxeoContextProvider {

	protected NuxeoContext nuxeoContext=null;

	public NuxeoContext getNuxeoContext() {
		if (nuxeoContext==null) {
			nuxeoContext = new NuxeoContext(this);
			nuxeoContext.setCacheManager(new DefaultResponseCacheManager(this));
			nuxeoContext.setDeferredUpdateManager(new DefaultDeferedUpdateManager(this));
		}
		return nuxeoContext;
	}
}
