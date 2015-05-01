package com.ng.apersist;

import java.util.Set;

import com.ng.apersist.util.NoPersistenceClassException;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteOpenDbHelper extends SQLiteOpenHelper {

	private DbRegistry registry;

	public SQLiteOpenDbHelper(Context context, String name, DbRegistry registry,
			int version) {
		super(context, name, null, version);
		this.registry = registry;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Set<Class<?>> classes = PersistenceClassCollector.getFromRegistry(registry);
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
		Set<Class<?>> classes = PersistenceClassCollector.getFromRegistry(registry);
		for (Class<?> persistenceClass: classes) {
			db.execSQL(SQLBuilder.createDropSql(persistenceClass));
		}
		onCreate(db);
	}
}
