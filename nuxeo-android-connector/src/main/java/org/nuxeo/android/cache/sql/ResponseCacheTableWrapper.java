/*
 * (C) Copyright 2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 */

package org.nuxeo.android.cache.sql;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.automation.client.cache.ResponseCacheEntry;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class ResponseCacheTableWrapper extends AbstractSQLTableWrapper {

	public static final String TBLNAME = "NuxeoCacheEntries";

	protected static final String KEY_COLUMN = "KEY";
	protected static final String CTYPE_COLUMN = "CTYPE";
	protected static final String CDISP_COLUMN = "CDISP";
	protected static final String RTYPE_COLUMN = "RTYPE";
	protected static final String RENTITY_COLUMN = "RENTITY";

	protected static final String CREATE_STATEMENT = "CREATE TABLE " + TBLNAME
			+ " (" + KEY_COLUMN + " TEXT, " + CTYPE_COLUMN + " TEXT, "
			+ CDISP_COLUMN + " TEXT, " + RTYPE_COLUMN + " TEXT, "
			+ RENTITY_COLUMN + " TEXT);";

	@Override
	public String getCreateStatement() {
		return CREATE_STATEMENT;
	}

	@Override
	public String getTableName() {
		return TBLNAME;
	}

	@Override
	public String getKeyColumnName() {
		return KEY_COLUMN;
	}

	protected void addEntry(SQLiteDatabase db, String key, ResponseCacheEntry entry ) {
		String sql = "INSERT INTO " + TBLNAME + " (" + KEY_COLUMN + ","
		+ CTYPE_COLUMN + ","
		+ CDISP_COLUMN + ","
		+ RTYPE_COLUMN + ","
		+ RENTITY_COLUMN + ")"
		+  " VALUES ("
		+ "'" + key + "',"
		+ "'" + entry.getReponseContentType() + "',"
		+ "'" + entry.getResponseContentDisposition() + "',"
		+ "'" + entry.getRequestMethod() + "',"
		+ "'" + entry.getRequestEntity() + "'"
		+ ");";

		execTransactionalSQL(db, sql);
	}

	protected void updateEntry(SQLiteDatabase db, String key, ResponseCacheEntry entry ) {
		String sql = "UPDATE " + TBLNAME + " set "
		+ CTYPE_COLUMN + " = "
		+ "'" + entry.getReponseContentType() + "',"
		+ CDISP_COLUMN + " = "
		+ "'" + entry.getResponseContentDisposition() + "',"
		+ RTYPE_COLUMN + " = "
		+ "'" + entry.getRequestMethod() + "',"
		+ RENTITY_COLUMN + " = "
		+ "'" + entry.getRequestEntity() + "'"
		+ " where " + KEY_COLUMN + " = "
		+ "'" + key + "';";

		execTransactionalSQL(db, sql);
	}

	public void storeCacheEntry(String key, ResponseCacheEntry entry) {
		SQLiteDatabase db = getWritableDatabase();

		String sql = "select count(*) from " + TBLNAME + " where " + KEY_COLUMN + "= '" + key + "'";
		SQLiteStatement statement = db.compileStatement(sql);
		try {
			long nb = statement.simpleQueryForLong();
			if (nb>0) {
				updateEntry(db, key, entry);
			} else {
				addEntry(db, key, entry);
			}
		}finally {
			statement.close();
		}
	}

	public ResponseCacheEntry getEntry(String key) {
		SQLiteDatabase db = getReadableDatabase();

		String sql = "select * from " + TBLNAME + " where " + KEY_COLUMN + "= '" + key + "'";
		Cursor cursor = db.rawQuery(sql,null);

		try {
			if (cursor.getCount()>0 && cursor.moveToFirst()) {
				String cType = cursor.getString(cursor.getColumnIndex(CTYPE_COLUMN));
				String cDisp = cursor.getString(cursor.getColumnIndex(CDISP_COLUMN));
				String rType = cursor.getString(cursor.getColumnIndex(RTYPE_COLUMN));
				String entity = cursor.getString(cursor.getColumnIndex(RENTITY_COLUMN));

				ResponseCacheEntry cEntry = new ResponseCacheEntry(cType, cDisp, null, null);
				cEntry.setRequestMethod(Integer.parseInt(rType));
				cEntry.setRequestEntity(entity);
				return cEntry;
			}
			else {
				return null;
			}
		} finally {
			if (cursor!=null) {
				cursor.close();
			}
		}
	}

	public List<String> getKeys() {

		List<String> keys = new ArrayList<String>();
		SQLiteDatabase db = getReadableDatabase();

		String sql = "select " + KEY_COLUMN + " from " + TBLNAME;
		Cursor cursor = db.rawQuery(sql,null);

		try {
			if (cursor.getCount()>0 && cursor.moveToFirst()) {
				do {
					keys.add(cursor.getString(cursor.getColumnIndex(KEY_COLUMN)));
				} while (cursor.moveToNext());
			}
			return keys;
		} finally {
			if (cursor!=null) {
				cursor.close();
			}
		}
	}

}
