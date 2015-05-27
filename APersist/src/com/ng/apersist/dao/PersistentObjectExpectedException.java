package com.ng.apersist.dao;

public class PersistentObjectExpectedException extends Exception {

	private Object object;

	public PersistentObjectExpectedException(Object nestedObject) {
		this.object = nestedObject;
	}

	@Override
	public String getMessage() {
		return "Object \"" + object + "\" of class \""
				+ object.getClass().getSimpleName() + "\" is not persistent";
	}
}
