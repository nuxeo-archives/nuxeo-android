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

import org.json.JSONObject;
import org.nuxeo.android.documentprovider.LazyDocumentsList;
import org.nuxeo.android.documentprovider.LazyDocumentsListImpl;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsListImpl;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;

public class DocumentProviderTableWrapper extends AbstractSQLTableWrapper {

    public static final String TBLNAME = "NuxeoDocumentProviders";

    protected static final String KEY_COLUMN = "NAME";

    protected static final String OPID_COLUMN = "OPERATIONID";

    protected static final String PARAMS_COLUMN = "PARAMS";

    protected static final String HEADERS_COLUMN = "HEADERS";

    protected static final String CTX_COLUMN = "CTX";

    protected static final String INPUT_TYPE_COLUMN = "INPUT_TYPE";

    protected static final String INPUT_REF_COLUMN = "INPUT_REF";

    protected static final String INPUT_BINARY_COLUMN = "INPUT_BIN";

    protected static final String PAGE_PARAM_COLUMN = "PAGEPARAM";

    protected static final String READ_ONLY_COLUMN = "READONLY";

    protected static final String CREATE_STATEMENT = "CREATE TABLE " + TBLNAME
            + " (" + KEY_COLUMN + " TEXT, " + OPID_COLUMN + " TEXT, "
            + PARAMS_COLUMN + " TEXT, " + HEADERS_COLUMN + " TEXT, "
            + CTX_COLUMN + " TEXT, " + PAGE_PARAM_COLUMN + " TEXT, "
            + READ_ONLY_COLUMN + " TEXT, " + INPUT_TYPE_COLUMN + " TEXT, "
            + INPUT_REF_COLUMN + " TEXT, " + INPUT_BINARY_COLUMN + " TEXT);";

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

    public LazyDocumentsList getStoredProvider(Session session, String name) {

        SQLiteDatabase db = getReadableDatabase();

        String sql = "select * from " + getTableName() + " where " + KEY_COLUMN
                + "='" + name + "'";
        Cursor cursor = db.rawQuery(sql, null);

        try {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                String operationKey = cursor.getString(cursor.getColumnIndex(KEY_COLUMN));
                String operationId = cursor.getString(cursor.getColumnIndex(OPID_COLUMN));
                String jsonParams = cursor.getString(cursor.getColumnIndex(PARAMS_COLUMN));
                String jsonHeaders = cursor.getString(cursor.getColumnIndex(HEADERS_COLUMN));
                String jsonCtx = cursor.getString(cursor.getColumnIndex(CTX_COLUMN));
                String inputType = cursor.getString(cursor.getColumnIndex(INPUT_TYPE_COLUMN));
                String inputRef = cursor.getString(cursor.getColumnIndex(INPUT_REF_COLUMN));
                Boolean inputBin = false;

                if (inputType != null) {
                    inputBin = new Boolean(
                            cursor.getString(cursor.getColumnIndex(INPUT_BINARY_COLUMN)));
                }

                OperationRequest request = OperationPersisterHelper.rebuildOperation(
                        session, operationId, jsonParams, jsonHeaders, jsonCtx,
                        inputType, inputRef, inputBin);
                Boolean readOnly = new Boolean(
                        cursor.getString(cursor.getColumnIndex(READ_ONLY_COLUMN)));
                String pageParam = cursor.getString(cursor.getColumnIndex(PAGE_PARAM_COLUMN));

                LazyDocumentsList result = null;
                if (readOnly) {
                    result = new LazyDocumentsListImpl(request, pageParam);
                } else {
                    result = new LazyUpdatableDocumentsListImpl(request,
                            pageParam);
                }
                result.setName(name);
                return result;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public void storeProvider(String name, LazyDocumentsList docList) {

        OperationRequest request = docList.getFetchOperation();
        String pageParam = docList.getPageParameterName();

        SQLiteDatabase db = getWritableDatabase();

        String sql = "INSERT INTO " + getTableName() + " (" + KEY_COLUMN + ","
                + OPID_COLUMN + "," + PARAMS_COLUMN + "," + HEADERS_COLUMN
                + "," + CTX_COLUMN + "," + PAGE_PARAM_COLUMN + ","
                + READ_ONLY_COLUMN;

        String operationId = request.getDocumentation().getId();
        String jsonParams = new JSONObject(request.getParameters()).toString();
        String jsonHeaders = new JSONObject(request.getHeaders()).toString();
        String jsonCtx = new JSONObject(request.getContextParameters()).toString();

        String sqlValues = " VALUES (" + "'" + name + "'," + "'" + operationId
                + "'," + "'" + jsonParams + "'," + "'" + jsonHeaders + "',"
                + "'" + jsonCtx + "'," + "'" + pageParam + "'," + "'"
                + new Boolean(docList.isReadOnly()).toString() + "'";

        if (request.getInput() != null) {
            String inputType = request.getInput().getInputType();
            String inputRef = request.getInput().getInputRef();
            String inputBin = new Boolean(request.getInput().isBinary()).toString();

            sql = sql + "," + INPUT_TYPE_COLUMN + "," + INPUT_REF_COLUMN + ","
                    + INPUT_BINARY_COLUMN;
            sqlValues = sqlValues + ",'" + inputType + "','" + inputRef + "','"
                    + inputBin + "'";
        }
        String insertQuery = sql + " ) " + sqlValues + ");";

        execTransactionalSQL(db, insertQuery);
    }

}
