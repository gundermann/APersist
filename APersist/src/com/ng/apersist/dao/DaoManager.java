package com.ng.apersist.dao;

import java.util.Map;
import java.util.Set;

import android.util.Log;

import com.ng.apersist.Database;

public class DaoManager {

	private static DaoManager manager;

	private Map<Class<?>, DAO<?>> daos;

	public static void init(Database db, Package pkg) {
		manager = new DaoManager(db, pkg);
	}

	public static DaoManager getInstance() {
		return manager;
	}

	public DaoManager(Database db, Package pkg) {
		initDaos(pkg, db);
	}

	private void initDaos(Package pkg, Database db) {
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
