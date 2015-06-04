package com.ng.apersist.dao;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.util.Log;

import com.ng.apersist.Database;
import com.ng.apersist.DbRegistry;
import com.ng.apersist.annotation.AnnotationInterpreter;
import com.ng.apersist.annotation.ToMany;
import com.ng.apersist.util.NoPersistenceClassException;
import com.ng.apersist.util.PersistenceClassCollector;

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
				.getToManyFields(persistenceClass);
		for (Iterator<Field> iterator = allColumnFields.iterator(); iterator
				.hasNext();) {
			Field field = iterator.next();
			if (AnnotationInterpreter.isToMany(field)) {
				ToMany annotation = AnnotationInterpreter.getToManyAnnotation(field);
				String table = AnnotationInterpreter.getHelperTable(persistenceClass, field);
				String idColumn = AnnotationInterpreter.getHelperIdColumn(persistenceClass);
				String foreignIdColumn = AnnotationInterpreter.getHelperIdColumn(annotation.target());
				DAO<?> foreignDao = DaoManager.getInstance().getDaoForType(annotation.target());
				helperDAOs.put(table, new HelperDao<>(table, idColumn, foreignIdColumn, database, foreignDao));
				
			}
		}
	}

}
