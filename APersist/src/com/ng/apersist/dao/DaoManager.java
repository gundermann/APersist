package com.ng.apersist.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.util.Log;

import com.ng.apersist.Database;
import com.ng.apersist.DbRegistry;

public class DaoManager {

	private static DaoManager manager;

	private Map<Class<?>, DAO<?>> daos;


	public static void init(Database db, DbRegistry registry) {
		manager = new DaoManager(db, registry);
		Log.i("DaoManager", "init DaoManager");
	}

	public static DaoManager getInstance() {
		return manager;
	}

	public DaoManager(Database db, DbRegistry registry) {
		daos = new HashMap<Class<?>, DAO<?>>();
		initDaos(registry, db);
	}

	private void initDaos(DbRegistry registry, Database db) {
		Set<Class<? extends DAO<?>>> daoClasses = registry.getRegistred().keySet();
		for (Class<? extends DAO<?>> daoClass : daoClasses) {
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
