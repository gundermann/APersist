package com.ng.apersist.annotation;

public @interface SpecificRelationTo {

	int minRelations() default 0;
	
	int maxRelations();
	
}
