package com.ng.apersist;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.util.Log;

import com.ng.apersist.dao.DAO;
import com.ng.apersist.dao.DaoManager;
import com.ng.apersist.interpreter.AnnotationInterpreter;
import com.ng.apersist.util.NoPersistenceClassException;

public class HelperDaoManager {

	static Map<String, HelperDao<?>> helperDAOs = new HashMap<String, HelperDao<?>>();

	public static HelperDao<?> getDAOForTable(String table) {
		return helperDAOs.get(table);
	}

	public static void init(Database database, DbRegistry registry) {
		Set<Class<?>> classes = PersistenceClassCollector
				.getFromRegistry(registry);
		for (Class<?> clazz : classes) {
			try {
				registerHelperDaoIfNecessary(clazz, database, registry);
			} catch (NoPersistenceClassException e) {
				Log.e("DaoManager",
						"Cannot create helper dao for " + clazz.getSimpleName());
			}
		}
		Log.i("DaoManager", "init HelperDaoManager");
	}

	private static void registerHelperDaoIfNecessary(Class<?> persistenceClass,
			Database database, DbRegistry registry)
			throws NoPersistenceClassException {
		List<Field> allColumnFields = AnnotationInterpreter
				.getAllColumnFields(persistenceClass);
		for (Iterator<Field> iterator = allColumnFields.iterator(); iterator
				.hasNext();) {
			Field field = iterator.next();
			if (AnnotationInterpreter.isToMany(field)) {
				String table = AnnotationInterpreter.getTable(persistenceClass)
						+ "2" + AnnotationInterpreter.getTable(field.getType());
				String idColumn = AnnotationInterpreter.getIdColumn(persistenceClass);
				DAO<?> foreignDao = DaoManager.getInstance().getDaoForType(field.getType());
				helperDAOs.put(table, new HelperDao<>(table, idColumn, database, foreignDao));
				
			}
		}
	}

}
