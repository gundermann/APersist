package com.ng.apersist;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import android.util.Log;

import com.ng.apersist.interpreter.AnnotationInterpreter;

public class ObjectCreator<T> {

	private Class<T> parameterType;

	public ObjectCreator(Class<T> parameterType) {
		this.parameterType = parameterType;
	}

	public T createNewObject(Map<String, String> columnToValueMap) {
		T newInstance = null;
		try {
			newInstance = parameterType.newInstance();
			fillObject(newInstance, columnToValueMap);
		} catch (InstantiationException | IllegalAccessException e) {
			Log.e("DAO", "Cannot instantiate");
		}
		return newInstance;
	}

	private void fillObject(T newInstance, Map<String, String> columnToValueMap) {
		Map<Field, Method> setters = AnnotationInterpreter
				.getSetterWithField(newInstance);
		for (Field field : setters.keySet()) {
			Method setter = setters.get(field);
			if (AnnotationInterpreter.isForeignKey(field)) {

			} else {
				try {
					setter.invoke(newInstance, getValueFromMap(columnToValueMap, field));
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Object getValueFromMap(Map<String, String> columnToValueMap,
			Field field) {
		String column = AnnotationInterpreter.getColumnToField(field);
		String value = columnToValueMap.get(column);
		Class<?> type = field.getType();
		if (type == Long.class)
			return Long.valueOf(value);
		else if(type == String.class)
			return value;
		else 
			return convertTypeFromString(type, value);
	}


	//TODO extract to TypeConverter
	private Object convertTypeFromString(Class<?> type, String stringToConvert) {
		if(type == Date.class)
			return convertToDate(stringToConvert);
		return null;
	}

	private Date convertToDate(String stringToConvert) {
		try {
			DateFormat format = new SimpleDateFormat("dd.MM.yyy-hh:mm:ss");
			return  format.parse(stringToConvert);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

}
