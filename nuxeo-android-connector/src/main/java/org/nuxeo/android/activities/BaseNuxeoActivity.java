package org.nuxeo.android.activities;

import org.nuxeo.android.context.NuxeoContext;
import org.nuxeo.ecm.automation.client.jaxrs.Session;

import android.app.Activity;

public class BaseNuxeoActivity extends Activity {

	protected NuxeoContext getNuxeoContext() {
		return NuxeoContext.get(getApplicationContext());
	}

	protected Session getNuxeoSession() {
		return getNuxeoContext().getSession();
	}
}
