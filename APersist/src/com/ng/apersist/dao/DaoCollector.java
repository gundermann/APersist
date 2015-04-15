package com.ng.apersist.dao;

import java.util.HashSet;
import java.util.Set;

import com.ng.apersist.util.ClassCollector;

public class DaoCollector {

	@SuppressWarnings("unchecked")
	public static Set<Class<DAO<?>>> collectFromPath(Package pkg) {
		Set<Class<DAO<?>>> daosClasses = new HashSet<Class<DAO<?>>>();
		Set<String> classNames = ClassCollector.collectFromPath(pkg);
		for (String className : classNames) {
			try {
				Class<?> potentialClass = Class.forName(className);
				if (potentialClass.getSuperclass() == DAO.class) {
					daosClasses.add((Class<DAO<?>>) potentialClass);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return daosClasses;
	}

}
