package com.ng.apersist.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.util.Log;

import com.ng.apersist.SQLNullValue;
import com.ng.apersist.interpreter.AnnotationInterpreter;

public class ValueHandler {

	private static final String DATE_PATTERN = "dd.MM.yyy-hh:mm:ss";

	public static String getDatabaseTypeAsSQLValueFromField(Object object,
			Field field) {
		Log.i("VALUES", "try to invoke " + field.getName() + " from "
				+ object.getClass().getSimpleName() );
		try {
			Method getter = AnnotationInterpreter.getGetter(object.getClass()
					.getDeclaredMethods(), field);
			Object objectFromGetter = getter.invoke(object);
			return convertDatabaseTypeToString(convertTypeToDatabaseType(objectFromGetter));
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | MethodNotFound e) {
			Log.e(ValueHandler.class.getName(), e.getMessage());
		}
		return null;
	}

	public static String convertDatabaseTypeToString(
			Object convertTypeToDatabaseType) {
		if (convertTypeToDatabaseType.getClass().equals(String.class))
			return "\"" + convertTypeToDatabaseType + "\"";
		else if (convertTypeToDatabaseType.getClass()
				.equals(SQLNullValue.class))
			return convertTypeToDatabaseType.toString();
		return String.valueOf(convertTypeToDatabaseType);
	}

	@SuppressLint("SimpleDateFormat")
	public static Object convertTypeToDatabaseType(Object type) {
		if (type == null) {
			return new SQLNullValue();
		} else if (isNumberType(type.getClass()))
			return (Number) type;
		else if (isDateType(type.getClass())) {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
			return sdf.format((Date) type);
		} else
			return type.toString();
	}

	private static boolean isDateType(Class<? extends Object> class1) {
		return class1.equals(Date.class);
	}

	private static boolean isNumberType(Class<? extends Object> class1) {
		return class1.equals(Integer.class)
				|| class1.getSimpleName().equals("Long")
				|| class1.equals(int.class) || class1.equals(double.class)
				|| class1.equals(Double.class);
	}

	public static Object getValueOfField(Object object, Field field) {
		try {
			Method getter = AnnotationInterpreter.getGetter(object.getClass()
					.getDeclaredMethods(), field);
			return getter.invoke(object);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | MethodNotFound e) {
			Log.e(ValueHandler.class.getName(), e.getMessage());
		}
		return null;
	}

	public static Object convertTypeFromString(Class<?> type,
			String stringToConvert) throws TypeNotSupportedException {
		Log.i("VALUES", "Try to convert String to "+ type.getSimpleName());
		if (stringToConvert == null)
			return null;
		else if (type.isEnum())
			return convertToEnum(type, stringToConvert);
		else if (type == Date.class)
			return convertToDate(stringToConvert);
		else if (type == Long.class)
			return Long.valueOf(stringToConvert);
		else if (type == String.class)
			return stringToConvert;
		else if (type == Boolean.class || type == boolean.class)
			return Boolean.parseBoolean(stringToConvert);
		else if (type == Integer.class)
			return Integer.valueOf(stringToConvert);
		else if (type == Double.class)
			return Double.valueOf(stringToConvert);
		throw new TypeNotSupportedException(type);
	}

	private static Object convertToEnum(Class<?> enumType,
			String stringToConvert) {
		Object[] enumConstants = enumType.getEnumConstants();
		for (int i = 0; i < enumConstants.length; i++) {
			if (enumConstants[i].toString().equals(stringToConvert))
				return enumConstants[i];
		}

		// TODO exception
		return null;
	}

	@SuppressLint("SimpleDateFormat")
	private static Date convertToDate(String stringToConvert) {
		try {
			DateFormat format = new SimpleDateFormat(DATE_PATTERN);
			return format.parse(stringToConvert);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Class<?> getDatabaseTypeFor(Class<?> parameterType) {
		if (isNumberType(parameterType))
			return Integer.class;
		else if (isDateType(parameterType))
			return Date.class;
		else
			return String.class;
	}

	public static String convertIterableToString(List<?> iterable) {
		StringBuilder sb = new StringBuilder();
		Iterator<?> i = iterable.iterator();
		sb.append(i.next().toString());
		while (i.hasNext()) {
			sb.append(";");
			Object next = i.next();
			sb.append(next.toString());
		}
		return sb.toString();
	}

}
