package com.ng.apersist;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public interface Database {

	/**
	 * 
	 * @param sql
	 * @return the result of sql as android cursor
	 */
	public Cursor execQuery(String sql);

	/**
	 * @return the wirteable database of the android database
	 */
	public SQLiteDatabase getWriteableDb();

}