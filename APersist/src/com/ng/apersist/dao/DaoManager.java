package com.ng.apersist.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.util.Log;

import com.ng.apersist.DatabaseImpl;

public class DaoManager {

	private static DaoManager manager;

	private Map<Class<?>, DAO<?>> daos;

	public static void init(DatabaseImpl db, Package pkg) {
		manager = new DaoManager(db, pkg);
	}

	public static DaoManager getInstance() {
		return manager;
	}

	public DaoManager(DatabaseImpl db, Package pkg) {
		daos = new HashMap<Class<?>, DAO<?>>();
		initDaos(pkg, db);
	}

	private void initDaos(Package pkg, DatabaseImpl db) {
		Set<Class<DAO<?>>> daoClasses = DaoCollector
				.collectFromPath(pkg);
		for (Class<DAO<?>> daoClass : daoClasses) {
			try {
				DAO<?> dao = daoClass.newInstance();
				dao.setDatabase(db);
				daos.put(dao.getParameterType(), dao);
			} catch (InstantiationException | IllegalAccessException e) {
				Log.e("DaoManager",
						"cannot instantiate dao: " + daoClass.getSimpleName());
			}
		}
	}

	public DAO<?> getDaoForType(Class<?> parameterType) {
		if (daos.keySet().contains(parameterType))
			return daos.get(parameterType);
		return null;
	}

}
