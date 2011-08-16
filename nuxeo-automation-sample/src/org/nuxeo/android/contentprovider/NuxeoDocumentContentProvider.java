package org.nuxeo.android.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class NuxeoDocumentContentProvider extends ContentProvider {

	public static final Uri CONTENT_URI = Uri.parse("content://org.nuxeo");

	public static final Uri CONTENT_URI_DOC = Uri
			.parse("content://org.nuxeo/document");

	public static final Uri CONTENT_URI_TASK = Uri
			.parse("content://org.nuxeo/task");

	protected UUIDMapper mapper;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

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
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// don't init the NuxeoSession now !!!
		mapper = new UUIDMapper();
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] columns, String selection,
			String[] selectionArgs, String sortOrder) {

		return new NuxeoDocumentCursor(getContext(),selection, selectionArgs, sortOrder,"common,dublincore", 5, mapper);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void notifyDa() {

	}

}
