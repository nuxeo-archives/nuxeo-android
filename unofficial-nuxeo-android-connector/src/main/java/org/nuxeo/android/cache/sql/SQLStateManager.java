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
