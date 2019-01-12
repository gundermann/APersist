package com.ng.apersist;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ng.apersist.query.NativeQuery;

public interface Database {

	/**
	 * @param sql
	 * @return the native query
	 */
	public NativeQuery createQuery(String sql );

	/**
	 * @return the wirteable database of the android database
	 */
	public SQLiteDatabase getWriteableDb();

}