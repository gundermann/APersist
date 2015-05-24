package com.ng.apersist.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to mark a field as id.
 * Works only with numeric types.
 * @author ngundermann
 *
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Id {
	
	boolean autoincrement() default false;
	
}
