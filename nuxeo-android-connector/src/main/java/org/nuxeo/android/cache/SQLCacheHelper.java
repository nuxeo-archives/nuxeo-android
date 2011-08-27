package org.nuxeo.android.cache;

import java.beans.Statement;

import org.nuxeo.ecm.automation.client.cache.CacheEntry;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class SQLCacheHelper extends SQLiteOpenHelper {

	protected static final int VERSION = 1;

	protected static final String DBNAME = "NuxeoCacheDB";

	protected static final String TBLNAME = "NuxeoCacheEntries";

	protected static final String KEY_COLUMN = "KEY";
	protected static final String CTYPE_COLUMN = "CTYPE";
	protected static final String CDISP_COLUMN = "CDISP";
	protected static final String RTYPE_COLUMN = "RTYPE";
	protected static final String RENTITY_COLUMN = "RENTITY";

	protected static final String CREATE_STATEMENT = "CREATE TABLE " + TBLNAME
			+ " (" + KEY_COLUMN + " TEXT, " + CTYPE_COLUMN + " TEXT, "
			+ CDISP_COLUMN + " TEXT, " + RTYPE_COLUMN + " TEXT, "
			+ RENTITY_COLUMN + " TEXT);";

	public SQLCacheHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_STATEMENT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	protected void addEntry(SQLiteDatabase db, String key, CacheEntry entry ) {
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

		db.beginTransaction();
		db.execSQL(sql);
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	protected void updateEntry(SQLiteDatabase db, String key, CacheEntry entry ) {
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

		db.beginTransaction();
		db.execSQL(sql);
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public void storeCacheEntry(String key, CacheEntry entry) {
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

	public long getEntryCount() {
		SQLiteDatabase db = getReadableDatabase();

		String sql = "select count(*) from " + TBLNAME ;
		SQLiteStatement statement=null;
		try {
			statement = db.compileStatement(sql);
			return statement.simpleQueryForLong();
		} finally {
			if (statement!=null) {
				statement.close();
			}
		}
	}

	public CacheEntry getEntry(String key) {
		SQLiteDatabase db = getReadableDatabase();

		String sql = "select * from " + TBLNAME + " where " + KEY_COLUMN + "= '" + key + "'";
		Cursor cursor = db.rawQuery(sql,null);

		try {
			if (cursor.getCount()>0 && cursor.moveToFirst()) {
				String cType = cursor.getString(cursor.getColumnIndex(CTYPE_COLUMN));
				String cDisp = cursor.getString(cursor.getColumnIndex(CDISP_COLUMN));
				String rType = cursor.getString(cursor.getColumnIndex(RTYPE_COLUMN));
				String entity = cursor.getString(cursor.getColumnIndex(RENTITY_COLUMN));

				CacheEntry cEntry = new CacheEntry(cType, cDisp, null, null);
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

	public void clear() {
		SQLiteDatabase db = getWritableDatabase();
		String sql = "delete  from " + TBLNAME ;
		db.beginTransaction();
		db.execSQL(sql);
		db.setTransactionSuccessful();
		db.endTransaction();
	}
}
