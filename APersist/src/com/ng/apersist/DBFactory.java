package com.ng.apersist;

import android.content.Context;

/**
 * Factory for the Databases you want to use in your application.
 * 
 * @author ngundermann
 *
 */
public class DBFactory {

	private static DBFactory _instance;

	public static DBFactory getInstance() {
		if (_instance == null)
			_instance = new DBFactory();
		return _instance;
	}

	/**
	 * Creates a new Database.
	 * 
	 * @param context
	 *            - Android application context
	 * @param name
	 *            - name of database
	 * @param registry
	 *            - DBRegistry with DAOs an persistence classes
	 * @param version
	 *            - version of the databases. Has to be incremented if something
	 *            has changed on the persistence classes.
	 * 
	 * @return the new database
	 */
	public Database createDatabase(Context context, String name,
			DbRegistry registry, int version) {
		return new DatabaseImpl(context, name, registry, version);
	}
}
