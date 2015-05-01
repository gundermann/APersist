package com.ng.apersist;

import java.util.HashMap;
import java.util.Map;

import com.ng.apersist.dao.DAO;

public abstract class DbRegistry {

	
	private Map<Class<? extends DAO<?>>, Class<?>> registryMap;

	protected DbRegistry(){
		registryMap = new HashMap<Class<? extends DAO<?>>, Class<?>>();
		setup();
	}
	
	abstract protected void setup();
	
	protected <D extends DAO<P>, P>void add(Class<D> daoClass, Class<P> persistenceClass) {
		registryMap.put(daoClass, persistenceClass);
	}
	
	public Map<Class<? extends DAO<?>>, Class<?>> getRegistred(){
		return registryMap;
	}
}
