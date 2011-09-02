package org.nuxeo.android.cache.sql;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLStateManager extends SQLiteOpenHelper {

	protected static final int VERSION = 1;

	protected static final String DBNAME = "NuxeoCaches";

	protected Map<String, SQLTableWrapper> tableWrappers = new HashMap<String, SQLTableWrapper>();

	public SQLStateManager(Context context) {
		super(context, DBNAME, null, VERSION);
	}

	public void registerWrapper(SQLTableWrapper wrapper) {
		tableWrappers.put(wrapper.getTableName(), wrapper);

		final SQLStateManager sm = this;

		wrapper.setDBAccessor(new SQLDBAccessor() {

			@Override
			public SQLiteDatabase getWritableDatabase() {
				return sm.getWritableDatabase();
			}

			@Override
			public SQLiteDatabase getReadableDatabase() {
				return sm.getReadableDatabase();
			}
		});
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (SQLTableWrapper wrapper : tableWrappers.values()) {
			db.execSQL(wrapper.getCreateStatement());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	public SQLTableWrapper getTableWrapper(String name) {
		return tableWrappers.get(name);
	}




}
