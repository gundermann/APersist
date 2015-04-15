package com.ng.apersist.dao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;

import com.ng.apersist.Database;
import com.ng.apersist.ObjectCreator;
import com.ng.apersist.SQLBuilder;
import com.ng.apersist.interpreter.AnnotationInterpreter;

/**
 * Supertype for DAOs. Supplies the insertion, update, delete and selection of
 * PersistenceObjects.
 * 
 * @author Niels Gundermann
 *
 * @param <T>
 *            PersistenceClass
 */
public abstract class DAO<T> {

	private Database database;
	
	public DAO(){}
	

	/**
	 * 
	 * @param database
	 *            Database to use
	 */
	public DAO(Database database) {
		this.database = database;
	}

	/**
	 * Loads all Entries of T from DB.
	 * 
	 * @return List<T> allEntries
	 */
	public List<T> loadAll() {
		List<T> all = new ArrayList<T>();
		ObjectCreator<T> oc = new ObjectCreator<T>(getParameterType());
		Cursor c = database.getWriteableDb().query(
				AnnotationInterpreter.getTable(getParameterType()),
				AnnotationInterpreter.getAllColumns(), "", null, null, null,
				null);
		if (c.moveToFirst()) {
			all.add(oc.createNewObject(c));
			while (c.moveToNext()) {
				all.add(oc.createNewObject(c));
			}
		}
		return all;
	}

	private T insert(T object) {
		insertOrUpdateChildren(object);
		try {
			if (isIdNotSet(object))
				setIdToObject(object);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		database.getWriteableDb().rawQuery(SQLBuilder.createInsertSql(object),
				null);
		return object;
	}

	private boolean isIdNotSet(T object) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Field idField = AnnotationInterpreter.getIdField(getParameterType());
		Method getter = AnnotationInterpreter.getGetter(
				getParameterType().getMethods(), idField);
		return getter.invoke(object) == null;
	}

	private void setIdToObject(T object) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Field idField = AnnotationInterpreter.getIdField(getParameterType());
		Method setter = AnnotationInterpreter.getSetter(
				getParameterType().getMethods(), idField);
		setter.invoke(object, generateNextId());

	}

	private Long generateNextId() {
		Cursor maxIdCursor = database.getWriteableDb().rawQuery(
				SQLBuilder.createMaxIdSelectionSql(getParameterType()), null);
		if (maxIdCursor.moveToFirst())
			return maxIdCursor.getLong(0);
		return 1L;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void insertOrUpdateChildren(T object) {
		List<Field> complexFields = AnnotationInterpreter
				.getComplexFields(object);
		for (Field field : complexFields) {
			Method getter = AnnotationInterpreter.getGetter(
					getParameterType().getMethods(), field);
			try {
				Object subObject = getter.invoke(object);
				DAO daoForSubType = DaoManager.getInstance().getDaoForType(getter
						.invoke(object).getClass());
				daoForSubType.insertOrUpdate(subObject);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * If ID of param exists in Table, this method will update the entry with
	 * the ID. Otherwise it will create a new entry.
	 * 
	 * Attention! Use only single id columns.
	 * 
	 * @param persistentObject
	 */
	public T insertOrUpdate(T object) {
		Field idField = AnnotationInterpreter.getIdField(getParameterType());
		Method getter = AnnotationInterpreter.getGetter(
				getParameterType().getMethods(), idField);
		try {
			Object idFieldValue = getter.invoke(object);
			if (idFieldValue != null && load(idFieldValue) != null) {
				return update(object);
			}
			return insert(object);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	private T update(T object) {
		insertOrUpdateChildren(object);
		database.getWriteableDb().rawQuery(SQLBuilder.createUpdateSql(object),
				null);
		return object;
	}

	/**
	 * Loads the object with the param ID from DB.
	 * 
	 * @param idFieldValue
	 * @return T object with id
	 */
	public T load(Object idFieldValue) {
		Field idField = AnnotationInterpreter.getIdField(getParameterType());
		String idColumn = AnnotationInterpreter.getColumnToField(idField);
		Map<String, Object> columnToValueMap = new HashMap<String, Object>();
		columnToValueMap.put(idColumn, idFieldValue);
		Cursor loaded = database.getWriteableDb().rawQuery(
				SQLBuilder.createSelectSql(columnToValueMap, getParameterType()),
				null);
		if(loaded.moveToFirst())
			return new ObjectCreator<T>(getParameterType()).createNewObject(loaded);
		return null;
	}
	
	abstract protected Class<T> getParameterType();


	public void setDatabase(Database db) {
		database = db;
	}

}