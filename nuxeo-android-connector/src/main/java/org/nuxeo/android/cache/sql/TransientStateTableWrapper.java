package org.nuxeo.android.cache.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.automation.client.cache.DocumentDeltaSet;
import org.nuxeo.ecm.automation.client.cache.OperationType;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;
import org.nuxeo.ecm.automation.client.jaxrs.util.JSONExporter;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TransientStateTableWrapper extends AbstractSQLTableWrapper {

	public static final String TBLNAME = "NuxeoTransientState";

	protected static final String KEY_COLUMN = "UID";
	protected static final String PATH_COLUMN = "PATH";
	protected static final String OPTYPE_COLUMN = "OPTYPE";
	protected static final String DOCTYPE_COLUMN = "DOCTYPE";
	protected static final String PROPS_COLUMN = "PROPS";
	protected static final String LISTNAME_COLUMN = "LISTNAME";
	protected static final String REQUESTID_COLUMN = "REQUESTID";


	protected static final String CREATE_STATEMENT = "CREATE TABLE " + TBLNAME
			+ " (" + KEY_COLUMN + " TEXT, " + PATH_COLUMN + " TEXT, "
			+ OPTYPE_COLUMN + " TEXT, " + DOCTYPE_COLUMN + " TEXT, "
			+ LISTNAME_COLUMN + " TEXT, " + REQUESTID_COLUMN + " TEXT, "
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
		+ PROPS_COLUMN + ") " ;

		sql = sql + " VALUES ("
		+ "'" + deltaSet.getId() + "',"
		+ "'" + deltaSet.getPath() + "',"
		+ "'" + deltaSet.getOperationType().toString() + "',"
		+ "'" + deltaSet.getDocType() + "',"
		+ "'" + JSONExporter.toJSON(deltaSet.getDirtyProps()) + "');";

		// XXX manage Blobs

		execTransactionalSQL(db, sql);

	}

	public List<DocumentDeltaSet> getDeltaSets(List<String> ids) {

		SQLiteDatabase db = getReadableDatabase();

		String sql = "select * from " + getTableName() + " where " + getKeyColumnName() + " IN (";
		for (String id : ids) {
			sql = sql + "'" + id + "', ";
		}
		sql = sql + "'');";
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
						props = new PropertyMap(readMapFromJson(jsonProps));
					}
					String listName = cursor.getString(cursor.getColumnIndex(LISTNAME_COLUMN));
					String requestId = cursor.getString(cursor.getColumnIndex(REQUESTID_COLUMN));

					DocumentDeltaSet delta = new DocumentDeltaSet(opType,uuid, path, docType, props, listName, requestId);
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


	protected Map<String, Object> readMapFromJson(String data) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			JSONObject jsonMap = new JSONObject(data);
			Iterator<String> keyIterator = jsonMap.keys();
			while (keyIterator.hasNext()) {
				String key = keyIterator.next();
				result.put(key, jsonMap.get(key));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}


	public void deleteEntryByRequestId(String key) {
		SQLiteDatabase db = getWritableDatabase();
		String sql = "delete  from " + getTableName() + " where " + REQUESTID_COLUMN + "='" + key + "'";
		execTransactionalSQL(db, sql);
	}


}
