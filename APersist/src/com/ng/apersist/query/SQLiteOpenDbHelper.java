package com.ng.apersist.query;

import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ng.apersist.DbRegistry;
import com.ng.apersist.util.NoPersistenceClassException;
import com.ng.apersist.util.PersistenceClassCollector;

public class SQLiteOpenDbHelper extends SQLiteOpenHelper {

	private DbRegistry registry;

	public SQLiteOpenDbHelper(Context context, String name,
			DbRegistry registry, int version) {
		super(context, name, null, version);
		this.registry = registry;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			Set<Class<?>> classes = PersistenceClassCollector
					.getFromRegistry(registry);
			List<String> mainTablesCreationSqls = SQLBuilder
					.createMainTablesCreationSqls(classes);
			List<String> mainHelperCreationSqls = SQLBuilder
					.createHelperTablesCreationSqls(classes);
			for (String sql : mainTablesCreationSqls) {
				db.execSQL(sql);
			}
			for (String sql : mainHelperCreationSqls) {
				db.execSQL(sql);
			}
		} catch (SQLException | NoPersistenceClassException e) {
			Log.e("DB creation", e.getMessage());
		}
		Log.i("DB creation", "created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			Set<Class<?>> classes = PersistenceClassCollector
					.getFromRegistry(registry);
			List<String> mainTablesCreationSqls = SQLBuilder
					.createMainTablesDropSqls(classes);
			List<String> mainHelperCreationSqls = SQLBuilder
					.createHelperTablesDropSqls(classes);
			for (String sql : mainHelperCreationSqls) {
				db.execSQL(sql);
			}
			for (String sql : mainTablesCreationSqls) {
				db.execSQL(sql);
			}
		} catch (SQLException | NoPersistenceClassException e) {
			Log.e("DB update", e.getMessage());
		}
		Log.i("DB update", "update from " + oldVersion + " to " + newVersion);
		onCreate(db);
		
	}
}
