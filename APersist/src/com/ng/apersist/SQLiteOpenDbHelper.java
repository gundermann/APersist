package com.ng.apersist;

import java.util.Set;

import com.ng.apersist.util.NoPersistenceClassException;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteOpenDbHelper extends SQLiteOpenHelper {

	private Package pkg;

	public SQLiteOpenDbHelper(Context context, String name, Package pkg,
			int version) {
		super(context, name, null, version);
		this.pkg = pkg;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Set<Class<?>> classes = PersistenceClassCollector.collectFromPath(pkg);
		for (Class<?> persistenceClass: classes) {
			try {
				db.execSQL(SQLBuilder.createCreateSql(persistenceClass));
			} catch (SQLException | NoPersistenceClassException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Set<Class<?>> classes = PersistenceClassCollector.collectFromPath(pkg);
		for (Class<?> persistenceClass: classes) {
			db.execSQL(SQLBuilder.createDropSql(persistenceClass));
		}
		onCreate(db);
	}
}
