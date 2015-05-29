package com.ng.apersist.interpreter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.util.Log;

import com.ng.apersist.annotation.Column;
import com.ng.apersist.annotation.Id;
import com.ng.apersist.annotation.PersistenceClass;
import com.ng.apersist.annotation.Table;
import com.ng.apersist.annotation.ToMany;
import com.ng.apersist.annotation.ToManyMinOne;
import com.ng.apersist.annotation.ToOne;
import com.ng.apersist.annotation.ToOneOrNone;
import com.ng.apersist.util.MethodNotFound;
import com.ng.apersist.util.NoPersistenceClassException;

public class AnnotationInterpreter {

	public static boolean hasToManyFields(Class<?> persistenceClass){
		return !getToManyFields(persistenceClass).isEmpty();
	}
	
	public static String getColumnToField(Field field) {
		return field.getName();
	}

	public static boolean isSimpleField(Field field) {
		Class<?> type = field.getType();
		return type == String.class || type == Long.class || type == Date.class
				|| type == Double.class || type == boolean.class || type == Integer.class
				|| type.isEnum();
	}

	public static List<Field> getComplexFields(Class<?> parameterType) {
		List<Field> complexFields = new ArrayList<Field>();
		Field[] declaredFields = parameterType.getDeclaredFields();
		for (int i = 0; i < declaredFields.length; i++) {
			if (!isSimpleField(declaredFields[i]))
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
		List<Field> allDatabaseFields = getAllDatabaseFields(parameterType);
		for (Field field : allDatabaseFields) {
				try {
					Method getter = getSetter(
							parameterType.getDeclaredMethods(), field);
					setterWithColumn.put(field, getter);
				} catch (MethodNotFound e) {
					Log.e(AnnotationInterpreter.class.getName(), e.getMessage());
				}
			}
		return setterWithColumn;
	}

	@SuppressLint("DefaultLocale")
	public static Method getSetter(Method[] methods, Field field)
			throws MethodNotFound {
		String fieldName = field.getName().toLowerCase();
		for (int i = 0; i < methods.length; i++) {
			String methodName = methods[i].getName().toLowerCase();
			if (methodName.substring(0, 3).equals("set")
					&& (methodName.substring(3).equals(fieldName) || methodName
							.substring(3).equals(fieldName.substring(2))))
				return methods[i];
		}
		throw new MethodNotFound("setter for " + fieldName);
	}

	public static String getTable(Class<?> parameterType)
			throws NoPersistenceClassException {
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
	public static Method getGetter(Method[] methods, Field field)
			throws MethodNotFound {
		String fieldName = field.getName().toLowerCase();
		for (int i = 0; i < methods.length; i++) {
			String methodName = methods[i].getName().toLowerCase();
			if (methodName.substring(0, 3).equals("get")
					&& methodName.substring(3).equals(fieldName))
				return methods[i];
			else if ((methodName.substring(0, 2).equals("is")
					&& methodName.equals(fieldName)) || (methodName.substring(0, 2).equals("is")
					&& methodName.substring(2).equals(fieldName)))
				return methods[i];
		}
		throw new MethodNotFound("getter for " + fieldName);
	}

	public static List<Field> getAllColumnFields(
			Class<? extends Object> persistenceClass) {
		List<Field> columnFields = new ArrayList<Field>();
		Field[] declaredFields = persistenceClass.getDeclaredFields();
		for (int i = 0; i < declaredFields.length; i++) {
			if (isIdField(declaredFields[i]) || isToOne(declaredFields[i]))
				columnFields.add(declaredFields[i]);
			else {
				Column annotation = declaredFields[i]
						.getAnnotation(Column.class);
				if (annotation != null)
					columnFields.add(declaredFields[i]);
			}

		}
		return columnFields;
	}

	public static boolean isIdField(Field field) {
		return field.getAnnotation(Id.class) != null;
	}

	public static boolean isAutoincrement(Field field) {
		Id annotation = field.getAnnotation(Id.class);
		return annotation.autoincrement();
	}

	public static boolean isToOne(Field field) {
		return (field.getAnnotation(ToOne.class) != null || field
				.getAnnotation(ToOneOrNone.class) != null);
	}

	public static boolean isMinOne(Field field) {
		return (field.getAnnotation(ToManyMinOne.class) != null || field
				.getAnnotation(ToOne.class) != null);
	}

	public static boolean isToMany(Field field) {
		return (field.getAnnotation(ToManyMinOne.class) != null || field
				.getAnnotation(ToMany.class) != null);
	}

	public static String getIdColumn(Class<?> persistenceClass) {
		return getColumnToField(getIdField(persistenceClass));
	}

	public static ToMany getToManyAnnotation(Field field) {
		return field.getAnnotation(ToMany.class);
	}

	public static List<Field> getToManyFields(Class<?> persistenceClass) {
		List<Field> columnFields = new ArrayList<Field>();
		Field[] declaredFields = persistenceClass.getDeclaredFields();
		for (int i = 0; i < declaredFields.length; i++) {
				ToMany toManyAnnotation = declaredFields[i]
						.getAnnotation(ToMany.class);
				ToManyMinOne toManyMinOneAnnotation = declaredFields[i]
						.getAnnotation(ToManyMinOne.class);
				if (toManyAnnotation != null || toManyMinOneAnnotation != null)
					columnFields.add(declaredFields[i]);
		}
		return columnFields;
	}

	public static String getHelperTable(Class<?> persistenceClass, Field field) throws NoPersistenceClassException {
		return getTable(persistenceClass)+"2"+ getTable(field.getAnnotation(ToMany.class).target());
	}

	public static String getHelperIdColumn(Class<?> persistenceClass) throws NoPersistenceClassException {
		return getTable(persistenceClass)+"_id";
	}

	public static List<Field> getAllDatabaseFields(Class<? extends Object> clazz) {
		List<Field> fields = new ArrayList<Field>();
		fields.addAll(getAllColumnFields(clazz));
		fields.addAll(getToManyFields(clazz));
		return fields;
	}

	public static Field getTargetField(Field field) {
		return getIdField(field.getType());
	}

	public static String getTargetFieldColumn(Field field) {
		return getColumnToField(getTargetField(field));
	}

	public static List<Field> getAllColumnFieldsWithoutID(
			Class<? extends Object> persistenceClass) {
		List<Field> columnFields = new ArrayList<Field>();
		Field[] declaredFields = persistenceClass.getDeclaredFields();
		for (int i = 0; i < declaredFields.length; i++) {
				Column annotation = declaredFields[i]
						.getAnnotation(Column.class);
				if (annotation != null)
					columnFields.add(declaredFields[i]);

		}
		return columnFields;
	}

}
