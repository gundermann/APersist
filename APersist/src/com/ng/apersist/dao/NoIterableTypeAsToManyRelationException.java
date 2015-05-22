package com.ng.apersist.dao;

public class NoIterableTypeAsToManyRelationException extends Exception {

	private Class<? extends Object> class1;

	public NoIterableTypeAsToManyRelationException(
			Class<? extends Object> class1) {
				this.class1 = class1;
	}

		@Override
		public String getMessage() {
			return class1.getSimpleName() +" is not iterable";
		}
	
}
