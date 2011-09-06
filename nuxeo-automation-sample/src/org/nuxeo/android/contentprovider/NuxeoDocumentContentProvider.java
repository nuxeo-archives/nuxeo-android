package org.nuxeo.android.contentprovider;

import org.nuxeo.android.cursor.UUIDMapper;

import android.net.Uri;

public class NuxeoDocumentContentProvider extends AbstractNuxeoReadOnlyContentProvider {

	public static final Uri CONTENT_URI = Uri.parse("content://org.nuxeo");

	public static final Uri CONTENT_URI_DOC = Uri
			.parse("content://org.nuxeo/document");

	public static final Uri CONTENT_URI_TASK = Uri
			.parse("content://org.nuxeo/task");

	protected UUIDMapper mapper;

	@Override
	public String getType(Uri uri) {
		if (uri.equals(CONTENT_URI_DOC)) {
			return "Document";
		} else if (uri.equals(CONTENT_URI)) {
			return "Task";
		} else {
			return "NuxeoStuff";
		}
	}

	@Override
	protected int getPageSize() {
		return 5;
	}

}
