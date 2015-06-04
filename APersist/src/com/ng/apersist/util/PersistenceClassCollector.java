package com.ng.apersist.util;

import java.util.HashSet;
import java.util.Set;

import com.ng.apersist.DbRegistry;
import com.ng.apersist.annotation.PersistenceClass;
import com.ng.apersist.dao.DAO;

public class PersistenceClassCollector {

	public static Set<Class<?>> collectFromPath(Package pkg, String apkName) {
		Set<Class<?>> persistenceClasses = new HashSet<Class<?>>();
		Set<String> classNames = ClassCollector.collectFromPath(pkg, apkName);
		for (String className : classNames) {
			try {
				Class<?> potentialClass = Class.forName(className);
				if (potentialClass.getAnnotation(PersistenceClass.class) != null) {
					persistenceClasses.add((Class<?>) potentialClass);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return persistenceClasses;
	}

	public static Set<Class<?>> getFromRegistry(DbRegistry registry) {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		for (Class<? extends DAO<?>> daoclass : registry.getRegistred().keySet()) {
			classes.add(registry.getRegistred().get(daoclass));
		}
		return classes;
	}
//	
//	Map<Integer, List<Class<?>>> map = new HashMap<Integer, List<Class<?>>>();
//	for (Class<? extends DAO<?>> daoclass : registry.getRegistred()
//			.keySet()) {
//		Class<?> persistenceClass = registry.getRegistred().get(daoclass);
//		Integer priority = getPriorityOfPersistenceClass(persistenceClass, map);
//		List<Class<?>> classes;
//		if (map.get(priority) == null) {
//			classes = new ArrayList<Class<?>>();
//		}
//		else{
//			classes = map.get(priority);
//		}
//		classes.add(persistenceClass);
//		map.put(priority, classes);
//	}
//	return map;

}
