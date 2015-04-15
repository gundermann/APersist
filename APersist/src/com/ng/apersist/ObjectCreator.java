package com.ng.apersist;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import android.database.Cursor;
import android.util.Log;

import com.ng.apersist.interpreter.AnnotationInterpreter;

public class ObjectCreator<T> {

	private Class<T> parameterType;

	public ObjectCreator(Class<T> parameterType) {
		this.parameterType = parameterType;
	}

	public T createNewObject(Cursor c) {
		T newInstance = null;
		try {
			newInstance = parameterType.newInstance();
			fillObject(newInstance, c);
		} catch (InstantiationException | IllegalAccessException e) {
			Log.e("DAO", "Cannot instantiate");
		}
		return newInstance;
	}
	
	private void fillObject(T newInstance, Cursor c) {
		Map<Field, Method> setters = AnnotationInterpreter
				.getSetterWithField(newInstance);
		for (Field field : setters.keySet()) {
			Method setter = setters.get(field);
			if (AnnotationInterpreter.isForeignKey(field)) {
				
			} else {
				try {
					setter.invoke(newInstance, getValueFromCursor(c, field));
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private Object getValueFromCursor(Cursor c, Field field) {
		String column = AnnotationInterpreter.getColumnToField(field);
		int index = c.getColumnIndex(column);
		Class<?> type = field.getType();
		if (type == String.class)
			return c.getString(index);
		else if (type == Long.class)
			return c.getLong(index);
		return null;
	}

}
