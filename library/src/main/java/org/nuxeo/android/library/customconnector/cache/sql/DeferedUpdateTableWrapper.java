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
import org.nuxeo.ecm.automation.client.cache.CachedOperationRequest;
import org.nuxeo.ecm.automation.client.cache.OperationType;
import org.nuxeo.ecm.automation.client.jaxrs.ExecutionDependencies;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;

import java.util.ArrayList;
import java.util.List;

public class DeferedUpdateTableWrapper extends AbstractSQLTableWrapper {

    public static final String TBLNAME = "NuxeoPendingUpdates";

    protected static final String KEY_COLUMN = "KEY";

    protected static final String OPID_COLUMN = "OPERATIONID";

    protected static final String OPTYPE_COLUMN = "OPTYPE";

    protected static final String PARAMS_COLUMN = "PARAMS";

    protected static final String HEADERS_COLUMN = "HEADERS";

    protected static final String DEPS_COLUMN = "DEPS";

    protected static final String CTX_COLUMN = "CTX";

    protected static final String INPUT_TYPE_COLUMN = "INPUT_TYPE";

    protected static final String INPUT_REF_COLUMN = "INPUT_REF";

    protected static final String INPUT_BINARY_COLUMN = "INPUT_BIN";

    protected static final String CREATE_STATEMENT = "CREATE TABLE " + TBLNAME
            + " (" + KEY_COLUMN + " TEXT, " + OPID_COLUMN + " TEXT, "
            + PARAMS_COLUMN + " TEXT, " + HEADERS_COLUMN + " TEXT, "
            + CTX_COLUMN + " TEXT, " + OPTYPE_COLUMN + " TEXT,"
            + INPUT_TYPE_COLUMN + " TEXT, " + INPUT_REF_COLUMN + " TEXT, "
            + INPUT_BINARY_COLUMN + " TEXT, " + DEPS_COLUMN + ");";

    @Override
    public String getCreateStatement() {
        return CREATE_STATEMENT;
    }

    @Override
    public String getTableName() {
        return TBLNAME;
    }

    @Override
    public String getKeyColumnName() {
        return KEY_COLUMN;
    }

    public OperationRequest storeRequest(String key, OperationRequest request,
            OperationType opType) {

        SQLiteDatabase db = getWritableDatabase();

        String sql = "INSERT INTO " + getTableName() + " (" + KEY_COLUMN + ","
                + OPID_COLUMN + "," + OPTYPE_COLUMN + "," + PARAMS_COLUMN + ","
                + HEADERS_COLUMN + "," + CTX_COLUMN + "," + DEPS_COLUMN;

        String operationId = request.getDocumentation().getId();
        String jsonParams = new JSONObject(request.getParameters()).toString();
        String jsonHeaders = new JSONObject(request.getHeaders()).toString();
        String jsonCtx = new JSONObject(request.getContextParameters()).toString();
        String deps = request.getDependencies().asJSON();

        String sqlValues = " VALUES (" + "'" + key + "'," + "'" + operationId
                + "'," + "'" + opType.toString() + "'," + "'" + jsonParams
                + "'," + "'" + jsonHeaders + "'," + "'" + jsonCtx + "'," + "'"
                + deps + "'";

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
        db.beginTransaction();
        db.execSQL(insertQuery);
        db.setTransactionSuccessful();
        db.endTransaction();

        return request;
    }

    public List<CachedOperationRequest> getPendingRequests(Session session) {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "select * from " + getTableName();
        Cursor cursor = db.rawQuery(sql, null);

        List<CachedOperationRequest> result = new ArrayList<CachedOperationRequest>();

        try {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {

                do {
                    String operationKey = cursor.getString(cursor.getColumnIndex(KEY_COLUMN));
                    String operationId = cursor.getString(cursor.getColumnIndex(OPID_COLUMN));
                    OperationType opType = OperationType.fromString(cursor.getString(cursor.getColumnIndex(OPTYPE_COLUMN)));
                    String jsonParams = cursor.getString(cursor.getColumnIndex(PARAMS_COLUMN));
                    String jsonHeaders = cursor.getString(cursor.getColumnIndex(HEADERS_COLUMN));
                    String jsonCtx = cursor.getString(cursor.getColumnIndex(CTX_COLUMN));
                    String inputType = cursor.getString(cursor.getColumnIndex(INPUT_TYPE_COLUMN));
                    String inputRef = cursor.getString(cursor.getColumnIndex(INPUT_REF_COLUMN));
                    String deps = cursor.getString(cursor.getColumnIndex(DEPS_COLUMN));
                    Boolean inputBin = false;
                    if (inputType != null) {
                        inputBin = new Boolean(
                                cursor.getString(cursor.getColumnIndex(INPUT_BINARY_COLUMN)));
                    }

                    /*
                     * OperationDocumentation op =
                     * session.getOperation(operationId);
                     * Map<String, String> params =
                     * JSONHelper.readMapFromJson(jsonParams);
                     * Map<String, String> headers =
                     * JSONHelper.readMapFromJson(jsonHeaders);
                     * Map<String, String> ctx =
                     * JSONHelper.readMapFromJson(jsonCtx);
                     * OperationInput input = null;
                     * if
                     * (!cursor.isNull(cursor.getColumnIndex(INPUT_TYPE_COLUMN
                     * ))) {
                     * final String inputType =
                     * cursor.getString(cursor.getColumnIndex
                     * (INPUT_TYPE_COLUMN));
                     * final String inputRef =
                     * cursor.getString(cursor.getColumnIndex
                     * (INPUT_REF_COLUMN));
                     * Boolean inputBin = new
                     * Boolean(cursor.getString(cursor.getColumnIndex
                     * (INPUT_BINARY_COLUMN)));
                     * if (inputBin) {
                     * input = new FileBlob(null);
                     * // XX read Binary here
                     * } else {
                     * input = new OperationInput() {
                     * @Override
                     * public boolean isBinary() {
                     * return false;
                     * }
                     * @Override
                     * public String getInputType() {
                     * return inputType;
                     * }
                     * @Override
                     * public String getInputRef() {
                     * return inputRef;
                     * }
                     * };
                     * }
                     * }
                     * OperationRequest deferredRequest = new
                     * DefaultOperationRequest((DefaultSession) session,op,
                     * params, headers, ctx,input);
                     */
                    OperationRequest deferredRequest = OperationPersisterHelper.rebuildOperation(
                            session, operationId, jsonParams, jsonHeaders,
                            jsonCtx, inputType, inputRef, inputBin);
                    if (deps != null) {
                        deferredRequest.getDependencies().merge(
                                ExecutionDependencies.fromJSON(deps));
                    }
                    result.add(new CachedOperationRequest(deferredRequest,
                            operationKey, opType));

                } while (cursor.moveToNext());
            }
            return result;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
