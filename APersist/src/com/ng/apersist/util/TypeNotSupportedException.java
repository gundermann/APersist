package com.ng.apersist.util;

public class TypeNotSupportedException extends Exception {

	private Class<?> type;

	public TypeNotSupportedException(Class<?> type) {
		this.type = type;
	}

	@Override
	public String getMessage() {
		return type.getSimpleName() + " is not yet supported";
	}
}
