package com.ng.apersist;

import java.util.HashSet;
import java.util.Set;

import com.ng.apersist.annotation.PersistenceClass;
import com.ng.apersist.util.ClassCollector;

public class PersistenceClassCollector {

	public static Set<Class<?>> collectFromPath(Package pkg) {
		Set<Class<?>> persistenceClasses = new HashSet<Class<?>>();
		Set<String> classNames = ClassCollector.collectFromPath(pkg);
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

}
