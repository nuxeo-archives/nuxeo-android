package org.nuxeo.android.pending;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.FileBlob;
import org.nuxeo.ecm.automation.client.jaxrs.model.OperationDocumentation;
import org.nuxeo.ecm.automation.client.jaxrs.model.OperationInput;
import org.nuxeo.ecm.automation.client.jaxrs.spi.DefaultOperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.spi.DefaultSession;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class StorageHelper extends SQLiteOpenHelper {

	protected static final int VERSION = 1;

	protected static final String DBNAME = "NuxeoCachePendingDB";

	protected static final String TBLNAME = "NuxeoPendingEntries";

	protected static final String KEY_COLUMN = "KEY";
	protected static final String OPID_COLUMN = "OPERATIONID";
	protected static final String PARAMS_COLUMN = "PARAMS";
	protected static final String HEADERS_COLUMN = "HEADERS";
	protected static final String CTX_COLUMN = "CTX";
	protected static final String INPUT_TYPE_COLUMN = "INPUT_TYPE";
	protected static final String INPUT_REF_COLUMN = "INPUT_REF";
	protected static final String INPUT_BINARY_COLUMN = "INPUT_BIN";

	protected static final String CREATE_STATEMENT = "CREATE TABLE " + TBLNAME
			+ " (" + KEY_COLUMN + " TEXT, " + OPID_COLUMN + " TEXT, "
			+ PARAMS_COLUMN + " TEXT, " + HEADERS_COLUMN + " TEXT, "
			+ CTX_COLUMN + " TEXT, "
			+ INPUT_TYPE_COLUMN + " TEXT, "
			+ INPUT_REF_COLUMN + " TEXT, "
			+ INPUT_BINARY_COLUMN + " TEXT);";

	public StorageHelper(Context context) {
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

	protected Map<String, String> readMapFromJson(String data) {
		Map<String, String> result = new HashMap<String, String>();
		try {
			JSONObject jsonMap = new JSONObject(data);
			Iterator<String> keyIterator = jsonMap.keys();
			while (keyIterator.hasNext()) {
				String key = keyIterator.next();
				result.put(key, jsonMap.getString(key));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}


		return result;
	}

	public OperationRequest storeRequest(String key, OperationRequest request) {

		SQLiteDatabase db = getWritableDatabase();

		String sql = "INSERT INTO " + TBLNAME + " (" + KEY_COLUMN + ","
		+ OPID_COLUMN + ","
		+ PARAMS_COLUMN + ","
		+ HEADERS_COLUMN + ","
		+ CTX_COLUMN ;

		String operationId = request.getDocumentation().getId();
		String jsonParams = new JSONObject(request.getParameters()).toString();
        String jsonHeaders = new JSONObject(request.getHeaders()).toString();
        String jsonCtx = new JSONObject(request.getContextParameters()).toString();

		String sqlValues =  " VALUES ("
		+ "'" + key + "',"
		+ "'" + operationId + "',"
		+ "'" + jsonParams + "',"
		+ "'" + jsonHeaders + "',"
		+ "'" + jsonCtx + "'";

        if (request.getInput()!=null) {
        	String inputType = request.getInput().getInputType();
        	String inputRef = request.getInput().getInputRef();
        	String inputBin = new Boolean(request.getInput().isBinary()).toString();

        	sql = sql + "," + INPUT_TYPE_COLUMN + "," + INPUT_REF_COLUMN + "," + INPUT_BINARY_COLUMN;
        	sqlValues = sqlValues + ",'" + inputType + "','" + inputRef + "','" + inputBin + "'";
        }
        String insertQuery = sql + " ) " + sqlValues + ");";
		db.beginTransaction();
		db.execSQL(insertQuery);
		db.setTransactionSuccessful();
		db.endTransaction();

		return request;
	}

	public Map<String, OperationRequest> getPendingRequests(Session session) {
		SQLiteDatabase db = getReadableDatabase();

		String sql = "select * from " + TBLNAME ;
		Cursor cursor = db.rawQuery(sql,null);

		Map<String, OperationRequest> result = new HashMap<String, OperationRequest>();

		try {
			if (cursor.getCount()>0 && cursor.moveToFirst()) {

				do {
					String operationKey = cursor.getString(cursor.getColumnIndex(KEY_COLUMN));
					String operationId = cursor.getString(cursor.getColumnIndex(OPID_COLUMN));
					String jsonParams = cursor.getString(cursor.getColumnIndex(PARAMS_COLUMN));
		            String jsonHeaders = cursor.getString(cursor.getColumnIndex(HEADERS_COLUMN));
		            String jsonCtx = cursor.getString(cursor.getColumnIndex(CTX_COLUMN));


					OperationDocumentation op = session.getOperation(operationId);
		            Map<String, String> params = readMapFromJson(jsonParams);
		            Map<String, String> headers = readMapFromJson(jsonHeaders);
		            Map<String, String> ctx = readMapFromJson(jsonCtx);

		            OperationInput input = null;
		            if (!cursor.isNull(cursor.getColumnIndex(INPUT_TYPE_COLUMN))) {
			            final String inputType = cursor.getString(cursor.getColumnIndex(INPUT_TYPE_COLUMN));
			            final String inputRef = cursor.getString(cursor.getColumnIndex(INPUT_REF_COLUMN));
			            Boolean inputBin = new Boolean(cursor.getString(cursor.getColumnIndex(INPUT_BINARY_COLUMN)));
			            if (inputBin) {
			            	input = new FileBlob(null);
			            	// XX read Binary here
			            } else {
			            	input = new OperationInput() {

								@Override
								public boolean isBinary() {
									return false;
								}

								@Override
								public String getInputType() {
									return inputType;
								}

								@Override
								public String getInputRef() {
									return inputRef;
								}
							};
			            }
		            }

					OperationRequest deferredRequest = new DefaultOperationRequest((DefaultSession) session,op, params, headers, ctx,input);
					result.put(operationKey, deferredRequest);

				} while (cursor.moveToNext());
			}
			return result;
		} finally {
			if (cursor!=null) {
				cursor.close();
			}
		}
	}

	public void deleteEntry(String key) {
		SQLiteDatabase db = getWritableDatabase();
		String sql = "delete  from " + TBLNAME + " where " + KEY_COLUMN + "='" + key + "'";
		db.beginTransaction();
		db.execSQL(sql);
		db.setTransactionSuccessful();
		db.endTransaction();
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
