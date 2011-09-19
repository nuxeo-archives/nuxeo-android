package org.nuxeo.android.contentprovider;

public class NuxeoDocumentContentProvider extends AbstractNuxeoReadOnlyContentProvider {

	@Override
	protected int getDefaultPageSize() {
		return 10;
	}

}
