package org.nuxeo.android.cache.sql;

import android.database.sqlite.SQLiteDatabase;

public interface SQLDBAccessor {

	SQLiteDatabase getWritableDatabase();

	SQLiteDatabase getReadableDatabase();

}
