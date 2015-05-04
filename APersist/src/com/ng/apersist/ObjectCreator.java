package com.ng.apersist;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import android.util.Log;

import com.ng.apersist.dao.DAO;
import com.ng.apersist.dao.DaoManager;
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
			try {
				Object value;
				if (AnnotationInterpreter.isForeignKey(field)) {
					DAO<?> daoForSubtype = DaoManager.getInstance()
							.getDaoForType(field.getType());
					value = daoForSubtype.load(getValueFromMap(
							columnToValueMap, field));
				} else {
					value = getValueFromMap(columnToValueMap, field);
				}
				setter.invoke(newInstance, value);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	private Object getValueFromMap(Map<String, String> columnToValueMap,
			Field field) {
		String column = AnnotationInterpreter.getColumnToField(field);
		String value = columnToValueMap.get(column);
		Class<?> type = field.getType();
		return ValueHandler.convertTypeFromString(type, value);
	}

}
