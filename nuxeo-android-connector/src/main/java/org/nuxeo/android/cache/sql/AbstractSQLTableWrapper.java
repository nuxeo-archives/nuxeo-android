package org.nuxeo.android.cache.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public abstract class AbstractSQLTableWrapper implements SQLTableWrapper {

	protected SQLDBAccessor accessor;

	@Override
	public abstract String getCreateStatement();

	@Override
	public abstract String getTableName();

	@Override
	public abstract String getKeyColumnName();

	@Override
	public void setDBAccessor(SQLDBAccessor accessor) {
		this.accessor = accessor;
	}

	protected SQLiteDatabase getWritableDatabase() {
		return accessor.getWritableDatabase();
	}

	protected SQLiteDatabase getReadableDatabase() {
		return accessor.getReadableDatabase();
	}

	protected void execTransactionalSQL(SQLiteDatabase db, String sql) {
		db.beginTransaction();
		db.execSQL(sql);
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	@Override
	public long getCount() {
		SQLiteDatabase db = getReadableDatabase();

		String sql = "select count(*) from " + getTableName() ;
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

	@Override
	public void clearTable() {
		SQLiteDatabase db = getWritableDatabase();
		String sql = "delete  from " + getTableName() ;
		execTransactionalSQL(db, sql);
	}

	@Override
	public void deleteEntry(String key) {
		SQLiteDatabase db = getWritableDatabase();
		String sql = "delete  from " + getTableName() + " where " + getKeyColumnName() + "='" + key + "'";
		execTransactionalSQL(db, sql);
	}


	protected void dump() {
		SQLiteDatabase db = getReadableDatabase();

		String sql = "select * from " + getTableName();
		Cursor cursor = db.rawQuery(sql,null);

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < cursor.getColumnCount(); i++ ) {
			sb.append( " | ");
			sb.append(cursor.getColumnName(i));
		}
		Log.i(this.getClass().getSimpleName(),sb.toString());
		if (cursor.getCount()>0 && cursor.moveToFirst()) {

			do {
				sb = new StringBuffer();
				for (int i = 0; i < cursor.getColumnCount(); i++ ) {
					sb.append( " | ");
					sb.append(cursor.getString(i));
				}
				Log.i(this.getClass().getSimpleName(),sb.toString());
			} while (cursor.moveToNext());

		}
		cursor.close();
	}

}
