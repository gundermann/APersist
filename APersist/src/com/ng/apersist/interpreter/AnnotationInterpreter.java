package com.ng.apersist.interpreter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;

import com.ng.apersist.annotation.Column;
import com.ng.apersist.annotation.ForeignKey;
import com.ng.apersist.annotation.Id;
import com.ng.apersist.annotation.PersistenceClass;
import com.ng.apersist.annotation.Table;

public class AnnotationInterpreter {

	public static boolean isForeignKey(Field field) {
		return (field.getAnnotation(ForeignKey.class) != null);
	}

	public static String getColumnToField(Field field) {
		if (isSimpleField(field)) {
			return field.getName();
		}
		// TODO Auto-generated method stub
		return null;
	}

	public static boolean isSimpleField(Field field) {
		Class<?> type = field.getType();
		return type == String.class || type == Long.class || type == Date.class;
	}

	public static List<Field> getComplexFields(Object object) {
		List<Field> complexFields = new ArrayList<Field>();

		return complexFields;
	}

	public static Field getIdField(Class<?> parameterType) {
		for (Field field : parameterType.getDeclaredFields()) {
			if (field.getAnnotation(Id.class) != null)
				return field;
		}
		return null;
	}

	public static String[] getAllColumns() {
		// TODO Auto-generated method stub
		return null;
	}

	public static Map<Field, Method> getSetterWithField(Object newInstance) {
		Map<Field, Method> setterWithColumn = new HashMap<Field, Method>();
		Field[] fields = newInstance.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Column columnAnnotation = fields[i].getAnnotation(Column.class);
			Id idAnnotation = fields[i].getAnnotation(Id.class);
			if (columnAnnotation != null || idAnnotation != null) {
				setterWithColumn.put(
						fields[i],
						getSetter(newInstance.getClass().getDeclaredMethods(),
								fields[i]));
			}
		}

		return setterWithColumn;
	}

	@SuppressLint("DefaultLocale")
	public static Method getSetter(Method[] methods, Field field) {
		String fieldName = field.getName().toLowerCase();
		for (int i = 0; i < methods.length; i++) {
			String methodName = methods[i].getName().toLowerCase();
			if (methodName.substring(0, 3).equals("set")
					&& methodName.substring(3).equals(fieldName))
				return methods[i];
		}
		return null;
	}

	public static String getTable(Class<?> parameterType) {
		PersistenceClass persistenceClassAnnotation = parameterType
				.getAnnotation(PersistenceClass.class);
		if (persistenceClassAnnotation == null) {
			// Exception
		}
		Table tableAnnotation = parameterType.getAnnotation(Table.class);
		if (tableAnnotation == null)
			return parameterType.getSimpleName();
		else
			return tableAnnotation.name();
	}

	public static Method getGetter(Method[] methods, Field field) {
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().toLowerCase()
					.contains(field.getName().toLowerCase())
					&& methods[i].getName().substring(0, 2).equals("get"))
				return methods[i];
		}
		return null;

	}

}
