package com.ng.apersist.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to mark a class as persistence class so that objects of this class
 * will become persistent in database.
 * 
 * @author ngundermann
 *
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface PersistenceClass {

}
