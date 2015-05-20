package com.ng.apersist.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface SpecificRelationTo {

	int minRelations() default 0;
	
	int maxRelations();
	
}
