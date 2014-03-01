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
        execTransactionalSQL(db, sql, null);
    }

    protected void execTransactionalSQL(SQLiteDatabase db, String sql,
            Object[] args) {
        db.beginTransaction();
        if (args == null) {
            db.execSQL(sql);
        } else {
            db.execSQL(sql, args);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public long getCount() {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "select count(*) from " + getTableName();
        SQLiteStatement statement = null;
        try {
            statement = db.compileStatement(sql);
            return statement.simpleQueryForLong();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    @Override
    public void clearTable() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "delete  from " + getTableName();
        execTransactionalSQL(db, sql);
    }

    @Override
    public void deleteEntry(String key) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "delete  from " + getTableName() + " where "
                + getKeyColumnName() + "='" + key + "'";
        execTransactionalSQL(db, sql);
    }

    protected void dump() {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "select * from " + getTableName();
        Cursor cursor = db.rawQuery(sql, null);

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            sb.append(" | ");
            sb.append(cursor.getColumnName(i));
        }
        Log.i(this.getClass().getSimpleName(), sb.toString());
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {

            do {
                sb = new StringBuffer();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    sb.append(" | ");
                    sb.append(cursor.getString(i));
                }
                Log.i(this.getClass().getSimpleName(), sb.toString());
            } while (cursor.moveToNext());

        }
        cursor.close();
    }

}
