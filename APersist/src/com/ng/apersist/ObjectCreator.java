package com.ng.apersist;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import android.util.Log;

import com.ng.apersist.interpreter.AnnotationInterpreter;
import com.ng.apersist.util.ValueHandler;

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
				.getSetterWithField(parameterType);
		for (Field field : setters.keySet()) {
			Method setter = setters.get(field);
			if (AnnotationInterpreter.isForeignKey(field)) {

			} else {
				try {
					Object valueFromMap = getValueFromMap(columnToValueMap, field);
					if(valueFromMap != null)
					setter.invoke(newInstance, valueFromMap);
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
		if(value == null)
			return null;
		else if (type == Long.class)
			return Long.valueOf(value);
		else if(type == String.class)
			return value;
		else 
			return ValueHandler.convertTypeFromString(type, value);
	}




}
