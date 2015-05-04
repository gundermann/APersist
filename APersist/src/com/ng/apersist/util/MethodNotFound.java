package com.ng.apersist.util;

public class MethodNotFound extends Exception {

	public MethodNotFound(String string) {
		super("Could not found " + string);
	}

}
