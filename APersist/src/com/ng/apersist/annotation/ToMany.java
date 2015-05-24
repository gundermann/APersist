package com.ng.apersist.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Annotation to mark a field as to many relationship to another type.
 * Type in persistence class has to be iterable.
 * @author ngundermann
 *
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ToMany {
	 /**
	  * @return the class where the to many relationship points to.
	  */
	Class<?> target();

}
