package org.nuxeo.android.contentprovider;

import org.nuxeo.android.cursor.UUIDMapper;

public class NuxeoDocumentContentProvider extends AbstractNuxeoReadOnlyContentProvider {

	protected UUIDMapper mapper;

	@Override
	protected int getPageSize() {
		return 10;
	}

}
