package com.ng.apersist;

import com.ng.apersist.dao.DaoManager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseImpl implements Database {

	SQLiteOpenHelper helper;
	
	public DatabaseImpl(Context context, String name, DbRegistry registry, int version) {
		helper = new SQLiteOpenDbHelper(context, name, registry, version );
		DaoManager.init(this, registry);
	}


	public SQLiteDatabase getWriteableDb() {
		return helper.getWritableDatabase();
	}

	/* (non-Javadoc)
	 * @see com.ng.apersist.Database#execQuery(java.lang.String)
	 */
	@Override
	public Cursor execQuery(String sql) {
		return getWriteableDb().rawQuery(sql, null);
	}


}
