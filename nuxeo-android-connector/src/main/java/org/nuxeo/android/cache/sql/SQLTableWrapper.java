package org.nuxeo.android.cache.sql;

public interface SQLTableWrapper {

	void setDBAccessor(SQLDBAccessor accessor);

	String getTableName();

	String getCreateStatement();

	String getKeyColumnName();

	long getCount();

	void clearTable();

	void deleteEntry(String key);
}

