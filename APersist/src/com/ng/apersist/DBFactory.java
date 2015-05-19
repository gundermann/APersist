package com.ng.apersist;

import android.content.Context;

public class DBFactory {

	private static DBFactory _instance;

	public static DBFactory getInstance() {
		if (_instance == null)
			_instance = new DBFactory();
		return _instance;
	}

	public Database createDatabase(Context context, String name,
			DbRegistry registry, int version) {
		return new DatabaseImpl(context, name, registry, version);
	}
}
