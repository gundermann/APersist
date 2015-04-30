package com.ng.apersist.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

import com.ng.apersist.interpreter.AnnotationInterpreter;

public class ValueHandler {

	private static final String DATE_PATTERN = "dd.MM.yyy-hh:mm:ss";

	public static String getDatabaseTypeAsStringFromField(Object object,
			Field field) {
		Method getter = AnnotationInterpreter.getGetter(object.getClass()
				.getDeclaredMethods(), field);
		try {
			Object objectFromGetter = getter.invoke(object);
			return convertDatabaseTypeToString(convertTypeToDatabaseType(objectFromGetter));
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String convertDatabaseTypeToString(
			Object convertTypeToDatabaseType) {
		return String.valueOf(convertTypeToDatabaseType);
	}

	@SuppressLint("SimpleDateFormat")
	public static Object convertTypeToDatabaseType(Object type) {
		if (isNumberType(type.getClass()))
			return (Long) type;
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
		return class1.equals(Integer.class) || class1.equals(Long.class)
				|| class1.equals(int.class) || class1.equals(double.class)
				|| class1.equals(Double.class);
	}

	public static Object getValueOfField(Object object, Field field) {
		Method getter = AnnotationInterpreter.getGetter(object.getClass()
				.getDeclaredMethods(), field);
		try {
			return getter.invoke(object);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object convertTypeFromString(Class<?> type,
			String stringToConvert) {
		if (type == Date.class)
			return convertToDate(stringToConvert);
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
			return Long.class;
		else if (isDateType(parameterType))
			return Date.class;
		else
			return String.class;
	}

}
