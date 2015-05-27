package com.ng.apersist.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to set a specific table name of a persistence class.
 * 
 * @author ngundermann
 *
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Table {

	String name();
}
