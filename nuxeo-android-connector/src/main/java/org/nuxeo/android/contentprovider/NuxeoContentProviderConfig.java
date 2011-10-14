package org.nuxeo.android.contentprovider;

import android.content.pm.ProviderInfo;

public class NuxeoContentProviderConfig {

	public static String DEFAULT_AUTHORITY = "nuxeo";
	public static String PROVIDER_NAME = "org.nuxeo.android.contentprovider.NuxeoDocumentContentProvider";
	protected static String authority = null;

	protected static ProviderInfo pinfo;

	public static void init(ProviderInfo info) {
		pinfo = info;
	}

	public static String getAuthority() {
		if (authority==null) {
			authority = pinfo.authority;
		}
		if (authority==null) {
			return DEFAULT_AUTHORITY;
		}
		return authority;
	}

}
