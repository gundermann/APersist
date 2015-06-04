package com.ng.apersist.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import android.util.Log;

import com.ng.apersist.annotation.AnnotationInterpreter;
import com.ng.apersist.dao.DAO;
import com.ng.apersist.dao.DaoManager;
import com.ng.apersist.dao.HelperDaoManager;

public class ObjectCreator<T> {

	private Class<T> parameterType;

	public ObjectCreator(Class<T> parameterType) {
		this.parameterType = parameterType;
	}

	public T createNewObject(Map<String, String> columnToValueMap) {
		Log.i("Creator",
				"Try to create object for class: "
						+ parameterType.getSimpleName());
		T newInstance = null;
		try {
			newInstance = parameterType.newInstance();
			fillObject(newInstance, columnToValueMap);
		} catch (InstantiationException | IllegalAccessException e) {
			Log.e("Creator", "Cannot instantiate");
		}
		return newInstance;
	}

	private void fillObject(T newInstance, Map<String, String> columnToValueMap) {
		Log.i("Creator", "Try to fill object");
		Map<Field, Method> setters = AnnotationInterpreter
				.getSetterWithField(parameterType);
		for (Field field : setters.keySet()) {
			Log.i("Creator", "Try to fill field: " + field.getName());
			Method setter = setters.get(field);
			try {
				Object value;
				if (AnnotationInterpreter.isToOne(field)) {
					DAO<?> daoForSubtype = DaoManager.getInstance()
							.getDaoForType(field.getType());
					Field idField = AnnotationInterpreter.getIdField(field
							.getType());
					value = daoForSubtype.load(getValueFromMap(
							columnToValueMap, field, idField.getType()));
				} else if (AnnotationInterpreter.isToMany(field)) {
					value = getCollectionOfToManyTarget(columnToValueMap, field);
				} else {
					value = getValueFromMap(columnToValueMap, field,
							field.getType());
				}
				setter.invoke(newInstance, value);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoPersistenceClassException | TypeNotSupportedException e) {
				Log.e("Creator", "Cannot set values: " + e.getMessage());
			}
		}
	}

	private Collection<?> getCollectionOfToManyTarget(
			Map<String, String> columnToValueMap, Field field)
			throws NoPersistenceClassException {
		String table = "";
		for (String tablename : columnToValueMap.keySet()) {
			if (tablename.equals(AnnotationInterpreter.getHelperTable(
					parameterType, field))) {
				table = tablename;
				break;
			}
		}
		String id = columnToValueMap.get(table);
		return HelperDaoManager.getDAOForTable(table).loadAll(id);
	}

	private Object getValueFromMap(Map<String, String> columnToValueMap,
			Field field, Class<?> type) throws TypeNotSupportedException {
		String column = AnnotationInterpreter.getColumnToField(field);
		String value = columnToValueMap.get(column);
		return ValueHandler.convertTypeFromString(type, value);
	}

}
