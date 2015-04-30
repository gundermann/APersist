package com.ng.apersist;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public interface Database {

	public Cursor execQuery(String sql);

	public SQLiteDatabase getWriteableDb();

}