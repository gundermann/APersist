package com.ng.apersist;

import com.ng.apersist.dao.DaoManager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database {

	SQLiteOpenHelper helper;
	
	public Database(Context context, String name, Package pkg) {
		int version = 1;
		helper = new SQLiteOpenDbHelper(context, name, pkg, version );
		DaoManager.init(this, pkg);
	}

	public SQLiteDatabase getWriteableDb() {
		return helper.getWritableDatabase();
	}


}
