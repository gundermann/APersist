package com.ng.apersist;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.ng.apersist.interpreter.AnnotationInterpreter;
import com.ng.apersist.util.ValueHandler;

public class ObjectComparator {

	public static boolean areEqual(Object o1, Object o2) {
		if((o1 == null && o2!= null) || (o1!=null && o2==null))
			return false;
		if(o1 == null && o2 == null)
			return true;
		if (objectsHaveSameClass(o1, o2)) {
			List<Field> allDatabaseFields = AnnotationInterpreter
					.getAllDatabaseFields(o1.getClass());
			for (Field field : allDatabaseFields) {
				Object value1 = ValueHandler.getValueOfField(o1, field);
				Object value2 = ValueHandler.getValueOfField(o2, field);
				
				if (value1 instanceof Iterable) {
					if (!areIterablesEqual((Iterable) value1, (Iterable) value2) && !areIterablesEqual((Iterable) value2, (Iterable) value1))
						return false;
				} else if (!AnnotationInterpreter.isSimpleField(field)) {
					if (!areEqual(value1, value2))
						return false;
				} else {
					if (!value1.equals(value2))
						return false;
				}
			}
		}
		return true;
	}

	private static boolean areIterablesEqual(Iterable value1, Iterable value2) {
		Iterator iterator1 = value1.iterator();
		while (iterator1.hasNext()) {
			boolean equalFound = false;
			Object nestedValue1 = iterator1.next();
			Iterator iterator2 = value2.iterator();
			while(iterator2.hasNext()){
				Object nestedValue2 = iterator2.next();
				if (areEqual(nestedValue1, nestedValue2)){
					equalFound = true;
					break;
				}
			}
			if (!equalFound) {
				return false;
			}
		}
		return true;
	}

	private static boolean objectsHaveSameClass(Object o1, Object o2) {
		return o1.getClass().equals(o2.getClass());
	}

	public static boolean containsById(Collection<?> allObjects, Object o2) {
		for (Object o1 : allObjects) {
			Object id1 = ValueHandler.getValueOfField(o1,
					AnnotationInterpreter.getIdField(o1.getClass()));
			Object id2 = ValueHandler.getValueOfField(o2,
					AnnotationInterpreter.getIdField(o2.getClass()));
			if (id1.equals(id2))
				return true;
		}
		return false;
	}

}
