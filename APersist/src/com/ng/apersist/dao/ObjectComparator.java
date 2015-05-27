package com.ng.apersist.dao;

import java.lang.reflect.Field;
import java.util.List;

import com.ng.apersist.interpreter.AnnotationInterpreter;
import com.ng.apersist.util.ValueHandler;

public class ObjectComparator {

	public static boolean areEqual(Object o1, Object o2) {
		if(objectsHaveSameClass(o1, o2)){
			List<Field> allDatabaseFields = AnnotationInterpreter.getAllDatabaseFields(o1.getClass());
			for (Field field : allDatabaseFields) {
				Object value1 = ValueHandler.getValueOfField(o1, field);
				Object value2 = ValueHandler.getValueOfField(o2, field);
				if(!value1.equals(value2))
				return false;
			}
		}
		return true;
	}

	private static boolean objectsHaveSameClass(Object o1, Object o2) {
		return o1.getClass().equals(o2.getClass());
	}


}
