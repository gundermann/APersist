package com.ng.apersist;

import java.util.HashMap;
import java.util.Map;

import com.ng.apersist.dao.DAO;

/**
 * This singleton class has to contain all DAOs and persistence classes used for
 * the application.
 * 
 * @author ngundermann
 *
 */
public abstract class DbRegistry {

	private Map<Class<? extends DAO<?>>, Class<?>> registryMap;

	protected DbRegistry() {
		registryMap = new HashMap<Class<? extends DAO<?>>, Class<?>>();
		setup();
	}

	/**
	 * This method has to add all the DAOs and persistence classes;
	 * 
	 */
	abstract protected void setup();

	/**
	 * Adds a DAO and a persistence class to the register.
	 * @param daoClass
	 * @param persistenceClass
	 */
	protected <D extends DAO<P>, P> void add(Class<D> daoClass,
			Class<P> persistenceClass) {
		registryMap.put(daoClass, persistenceClass);
	}

	/**
	 * @return map of all registered DAOs and persistence classes.
	 * Key - DAO
	 * Value PersistenceClass
	 */
	public Map<Class<? extends DAO<?>>, Class<?>> getRegistred() {
		return registryMap;
	}
}
