package org.nuxeo.android.contentprovider;

import org.nuxeo.android.adapters.UUIDMapper;

public class NuxeoDocumentContentProvider extends AbstractNuxeoReadOnlyContentProvider {

	protected UUIDMapper mapper;

	@Override
	protected int getPageSize() {
		return 10;
	}

}
