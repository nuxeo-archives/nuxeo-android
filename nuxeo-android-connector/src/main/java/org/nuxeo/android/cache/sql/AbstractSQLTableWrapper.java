package org.nuxeo.android.cache.sql;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

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

}
