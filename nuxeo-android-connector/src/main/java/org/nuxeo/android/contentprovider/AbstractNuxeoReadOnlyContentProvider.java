package org.nuxeo.android.contentprovider;

import org.nuxeo.android.context.NuxeoContext;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public abstract class AbstractNuxeoReadOnlyContentProvider extends ContentProvider {

	protected UUIDMapper mapper;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException("Not implemented");
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

		String nxql = buildNXQLQuery(selection, selectionArgs, sortOrder);
		return buildCursor(nxql, selection, selectionArgs, sortOrder);
	}

	protected NuxeoDocumentCursor buildCursor(String nxql, String selection,String[] selectionArgs, String sortOrder) {
		return new NuxeoDocumentCursor(NuxeoContext.get(getContext()).getSession(),nxql, selectionArgs, sortOrder,getSchemas(), getPageSize(), mapper);
	}

	protected abstract int getPageSize();

	protected String getSchemas() {
		return "common,dublincore";
	}

	protected String buildNXQLQuery(String selection,
			String[] selectionArgs, String sortOrder) {

		String nxql = "select * from Document ";
		if (selection!=null) {
			nxql = nxql + " where " + selection;
		}

		if (sortOrder!=null && !sortOrder.equals("")) {
			nxql = nxql + " order by " + sortOrder;
		}

		return nxql;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		throw new UnsupportedOperationException("Not implemented");
	}


}
