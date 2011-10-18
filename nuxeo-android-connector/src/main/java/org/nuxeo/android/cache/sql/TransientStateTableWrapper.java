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

import org.nuxeo.ecm.automation.client.cache.DocumentDeltaSet;
import org.nuxeo.ecm.automation.client.cache.OperationType;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;
import org.nuxeo.ecm.automation.client.jaxrs.util.JSONExporter;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TransientStateTableWrapper extends AbstractSQLTableWrapper {

	public static final String TBLNAME = "NuxeoTransientState";

	protected static final String KEY_COLUMN = "UID";
	protected static final String PATH_COLUMN = "PATH";
	protected static final String OPTYPE_COLUMN = "OPTYPE";
	protected static final String DOCTYPE_COLUMN = "DOCTYPE";
	protected static final String PROPS_COLUMN = "PROPS";
	protected static final String LISTNAME_COLUMN = "LISTNAME";
	protected static final String REQUESTID_COLUMN = "REQUESTID";
	protected static final String CONFLICT_COLUMN = "CONFLICT";


	protected static final String CREATE_STATEMENT = "CREATE TABLE " + TBLNAME
			+ " (" + KEY_COLUMN + " TEXT, " + PATH_COLUMN + " TEXT, "
			+ OPTYPE_COLUMN + " TEXT, " + DOCTYPE_COLUMN + " TEXT, "
			+ LISTNAME_COLUMN + " TEXT, " + REQUESTID_COLUMN + " TEXT, "
			+ CONFLICT_COLUMN + " TEXT, "
			+ PROPS_COLUMN + " TEXT); ";

	@Override
	public String getCreateStatement() {
		return CREATE_STATEMENT;
	}

	@Override
	public String getKeyColumnName() {
		return KEY_COLUMN;
	}

	@Override
	public String getTableName() {
		return TBLNAME;
	}

	public void storeDeltaSet(DocumentDeltaSet deltaSet) {

		SQLiteDatabase db = getWritableDatabase();

		String sql = "INSERT INTO " + getTableName() + " (" + KEY_COLUMN + ","
		+ PATH_COLUMN + ","
		+ OPTYPE_COLUMN + ","
		+ DOCTYPE_COLUMN + ","
		+ PROPS_COLUMN + ","
		+ REQUESTID_COLUMN + ","
		+ LISTNAME_COLUMN + ") " ;

		sql = sql + " VALUES ("
		+ "'" + deltaSet.getId() + "',"
		+ "'" + deltaSet.getPath() + "',"
		+ "'" + deltaSet.getOperationType().toString() + "',"
		+ "'" + deltaSet.getDocType() + "',"
		+ "'" + JSONExporter.toJSON(deltaSet.getDirtyProps()) + "',"
		+ "'" + deltaSet.getRequestId() + "',"
		+ "'" + deltaSet.getListName() + "');";

		// blobs are stored indirectly in the BlobStore and referenced via a JSONBlob in the properties of the Document

		execTransactionalSQL(db, sql);
	}



	public List<DocumentDeltaSet> getDeltaSets(List<String> ids, String targetListName) {

		SQLiteDatabase db = getReadableDatabase();

		String sql = "select * from " + getTableName() + " where " + getKeyColumnName() + " IN (";
		for (String id : ids) {
			sql = sql + "'" + id + "', ";
		}
		sql = sql + "'') ";
		if (targetListName!=null) {
			sql = sql +" or " + LISTNAME_COLUMN + "='" + targetListName + "'";
		}
		sql = sql +";";
		Cursor cursor = db.rawQuery(sql,null);

		List<DocumentDeltaSet> result = new ArrayList<DocumentDeltaSet>();

		try {
			if (cursor.getCount()>0 && cursor.moveToFirst()) {

				do {
					String uuid = cursor.getString(cursor.getColumnIndex(KEY_COLUMN));
					String path = cursor.getString(cursor.getColumnIndex(PATH_COLUMN));
					OperationType opType = OperationType.fromString(cursor.getString(cursor.getColumnIndex(OPTYPE_COLUMN)));
					String docType = cursor.getString(cursor.getColumnIndex(DOCTYPE_COLUMN));
					PropertyMap props = null;
					String jsonProps =cursor.getString(cursor.getColumnIndex(PROPS_COLUMN));
					if (jsonProps!=null) {
						props = JSONExporter.getFromJSONString(jsonProps);
					}
					String listName = cursor.getString(cursor.getColumnIndex(LISTNAME_COLUMN));
					String requestId = cursor.getString(cursor.getColumnIndex(REQUESTID_COLUMN));
					String conflict = cursor.getString(cursor.getColumnIndex(CONFLICT_COLUMN));

					DocumentDeltaSet delta = new DocumentDeltaSet(opType,uuid, path, docType, props, requestId, listName);
					if (conflict!=null) {
						delta.setConflict(Boolean.parseBoolean(conflict));
					}
					result.add(delta);
				} while (cursor.moveToNext());
			}
		return result;
		} finally {
			if (cursor!=null) {
				cursor.close();
			}
		}
	}

	public void deleteEntryByRequestId(String key) {
		SQLiteDatabase db = getWritableDatabase();
		String sql = "delete  from " + getTableName() + " where " + REQUESTID_COLUMN + "='" + key + "'";
		execTransactionalSQL(db, sql);
	}

	public void updateConflictMarker(String uid, boolean conflict) {
		//dump();
		SQLiteDatabase db = getWritableDatabase();
		String strConflict = new Boolean(conflict).toString();
		String sql = "update  " + getTableName() + " set " + CONFLICT_COLUMN + "= '" + strConflict +  "'  where " + KEY_COLUMN + "='" + uid + "'";
        Log.i(this.getClass().getSimpleName(), sql);
		execTransactionalSQL(db, sql);
	}

}
