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
import com.ng.apersist.util.NoPersistenceClassException;

public class AnnotationInterpreter {

	public static boolean isForeignKey(Field field) {
		return (field.getAnnotation(ForeignKey.class) != null);
	}

	public static String getColumnToField(Field field) {
		if (isSimpleField(field)) {
			return field.getName();
		}
		else{
			return field.getName() + "_id";
		}
	}

	public static boolean isSimpleField(Field field) {
		Class<?> type = field.getType();
		return type == String.class || type == Long.class || type == Date.class;
	}

	public static List<Field> getComplexFields(Class<?> parameterType) {
		List<Field> complexFields = new ArrayList<Field>();
		Field[] declaredFields = parameterType.getDeclaredFields();
		for (int i = 0; i < declaredFields.length; i++) {
			if(!isSimpleField(declaredFields[i]))
				complexFields.add(declaredFields[i]);
		}
		return complexFields;
	}

	public static Field getIdField(Class<?> parameterType) {
		for (Field field : parameterType.getDeclaredFields()) {
			if (field.getAnnotation(Id.class) != null)
				return field;
		}
		return null;
	}

	public static String[] getAllColumns(Class<?> parameterType) {
		Field[] declaredFields = parameterType.getDeclaredFields();
		String[] columns = new String[declaredFields.length];
		for (int i = 0; i < columns.length; i++) {
			columns[i] = getColumnToField(declaredFields[i]);
		}
		return columns;
	}

	public static Map<Field, Method> getSetterWithField(Class<?> parameterType) {
		Map<Field, Method> setterWithColumn = new HashMap<Field, Method>();
		Field[] fields = parameterType.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Column columnAnnotation = fields[i].getAnnotation(Column.class);
			Id idAnnotation = fields[i].getAnnotation(Id.class);
			if (columnAnnotation != null || idAnnotation != null) {
				setterWithColumn.put(
						fields[i],
						getSetter(parameterType.getDeclaredMethods(),
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

	public static String getTable(Class<?> parameterType) throws NoPersistenceClassException {
		PersistenceClass persistenceClassAnnotation = parameterType
				.getAnnotation(PersistenceClass.class);
		if (persistenceClassAnnotation == null) {
			throw new NoPersistenceClassException(parameterType);
		}
		Table tableAnnotation = parameterType.getAnnotation(Table.class);
		if (tableAnnotation == null)
			return parameterType.getSimpleName();
		else
			return tableAnnotation.name();
	}

	@SuppressLint("DefaultLocale")
	public static Method getGetter(Method[] methods, Field field) {
		String fieldName = field.getName().toLowerCase();
		for (int i = 0; i < methods.length; i++) {
			String methodName = methods[i].getName().toLowerCase();
			if (methodName.substring(0, 3).equals("get")
					&& methodName.substring(3).equals(fieldName))
				return methods[i];
		}
		return null;
	}

	public static List<Field> getAllColumnFields(Class<? extends Object> class1) {
		return null;
		// TODO Auto-generated method stub
		
	}

	public static boolean isIdField(Field field) {
		return field.getAnnotation(Id.class) != null;
	}

	public static boolean isAutoincrement(Field field) {
		Id annotation = field.getAnnotation(Id.class);
		return annotation.autoincrement();
	}

	public static String getTargetField(Field field) {
		ForeignKey annotation = field.getAnnotation(ForeignKey.class);
		return annotation.targetField();
	}


}
