package com.ng.apersist.util;

public class NoPersistenceClassException extends Exception {


	private static final long serialVersionUID = -4446732071074956378L;

	public NoPersistenceClassException(Class<?> parameterType) {
		super(parameterType.getName() + " is not annotated as PersistenceClass.");
	}

	

	
}
