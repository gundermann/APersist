package com.ng.apersist.dao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.util.Log;

import com.ng.apersist.Database;
import com.ng.apersist.annotation.AnnotationInterpreter;
import com.ng.apersist.query.SQLBuilder;
import com.ng.apersist.util.MethodNotFound;
import com.ng.apersist.util.NoPersistenceClassException;
import com.ng.apersist.util.ObjectComparator;
import com.ng.apersist.util.ObjectCreator;
import com.ng.apersist.util.ValueExtractor;
import com.ng.apersist.util.ValueHandler;

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

	public DAO() {
	}

	/**
	 * 
	 * @param database
	 *            Database to use
	 */
	public DAO(Database database) {
		this.database = database;
	}


    public Collection<T> loadFromCursor( Cursor cursor ) {
        Collection<T> result = new ArrayList<>();
        cursor.moveToFirst();
        do {
            result.add( createObject( cursor ) );
        } while ( cursor.moveToNext() );

        return result;
    }


    /**
	 * Loads all Entries of T from DB.
	 * 
	 * @return List<T> allEntries
	 */
	public List<T> loadAll() {
		List<T> all = new ArrayList<T>();
		Log.i("DAO", "Try to load all of " + getParameterType().getSimpleName());
		try {
			Cursor c = database.getWriteableDb().rawQuery(
					SQLBuilder.createSelectSql(null, getParameterType()), null);
			if (c.moveToFirst()) {
				do {
					all.add(createObject(c));
				} while (c.moveToNext());
			}
		} catch (NoPersistenceClassException e) {
			Log.e("Database", e.getMessage());
		}
		return all;
	}

	public boolean delete(T object) {
		Log.i("DAO", "Try to delete " + object.toString() + " from class "
				+ object.getClass().getSimpleName());
		try {
			database.getWriteableDb().execSQL(
					SQLBuilder.createDeleteSql(object));
			return true;
		} catch (NoPersistenceClassException e) {
			Log.e("Database", e.getMessage());
		}
		return false;
	}

	private T insert(T object) {
		Log.i("DAO", "Try to insert " + object.toString() + " from class "
				+ object.getClass().getSimpleName());
		insertOrUpdateChildren(object);
		try {
			if (isIdNotSet(object))
				setIdToObject(object);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		try {
			database.getWriteableDb().execSQL(
					SQLBuilder.createInsertSql(object));
		} catch (NoPersistenceClassException e) {
			Log.e("Database", e.getMessage());
		}
		return object;
	}

	private boolean isIdNotSet(T object) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Field idField = AnnotationInterpreter.getIdField(getParameterType());
		Method getter;
		try {
			getter = AnnotationInterpreter.getGetter(getParameterType()
					.getMethods(), idField);
			return getter.invoke(object) == null;
		} catch (MethodNotFound e) {
			Log.e(DAO.class.getName(), e.getMessage());
		}
		return false;
	}

	private void setIdToObject(T object) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Log.i("DAO", "Set id to object " + object.toString() + " from class "
				+ object.getClass().getSimpleName());
		Field idField = AnnotationInterpreter.getIdField(getParameterType());
		Method setter;
		try {
			setter = AnnotationInterpreter.getSetter(getParameterType()
					.getMethods(), idField);
			setter.invoke(object, generateNextId());
		} catch (MethodNotFound e) {
			Log.e(DAO.class.getName(), e.getMessage());
		}
	}

	private Long generateNextId() {
		Long maxId = getMaxId();
		return maxId != null ? maxId + 1 : 0L;
	}

	public Long getMaxId() {
		Log.i("DAO", "Try to get max id from "+ getParameterType().getSimpleName());
		try {
            Iterator<Object[]> maxIdIterator = database.createQuery(SQLBuilder
                    .createMaxIdSelectionSql(getParameterType())).getResult().iterator();
			if (maxIdIterator.hasNext())
				return Integer.class.cast(maxIdIterator.next()[0]).longValue();
		} catch (NoPersistenceClassException e) {
			Log.e("Database", e.getMessage());
		}
		return null;
	}

	private void insertOrUpdateChildren(T object) {
		List<Field> complexFields = AnnotationInterpreter
				.getComplexFields(getParameterType());
		for (Field field : complexFields) {
			try {
				if (AnnotationInterpreter.isToMany(field))
					insertOrUpdateComplexFieldWithToManyRealation(object, field);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | MethodNotFound
					| NoIterableTypeAsToManyRelationException
					| NoPersistenceClassException
					| PersistentObjectExpectedException e) {
				Log.e(DAO.class.getName(), e.getMessage());
			}
		}

	}

	private void insertOrUpdateComplexFieldWithToManyRealation(T object,
			Field field) throws MethodNotFound, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoIterableTypeAsToManyRelationException,
			NoPersistenceClassException, PersistentObjectExpectedException {
		Object subObject = ValueHandler.getValueOfField(object, field);
		if (subObject == null) {

		} else if (!(subObject instanceof Iterable)) {
			throw new NoIterableTypeAsToManyRelationException(
					subObject.getClass());
		} else {
			Iterable<?> iterabeSubObject = (Iterable<?>) subObject;
			HelperDao<?> helperDao = HelperDaoManager
					.INSTANCE.getDAOForTable(AnnotationInterpreter.getHelperTable(
							getParameterType(), field));
			Object objectId = getIdOfObject(object);
			if (objectId == null)
				objectId = generateNextId();
			
			helperDao.deleteAll(String.valueOf(objectId));
			addRelations(iterabeSubObject.iterator(), 
					objectId, helperDao);
		}
	}

//	private void deleteRelations(Collection<?> allToManyObjects,
//			Iterator<?> subObjectIterator, Object objectId,
//			HelperDao<?> helperDao) {
//		
//		List<Object> nestedObjects = new ArrayList<Object>();
//		while (subObjectIterator.hasNext()) {
//			Object nestedObject = subObjectIterator.next();
//			nestedObjects.add(nestedObject);
//		}
//		for (Object toManyObject : allToManyObjects) {
//			if (toManyObject == null || !ObjectComparator.containsById(nestedObjects, toManyObject)) {
//				Object subObjectId = getIdOfObject(toManyObject);
//				helperDao.delete(String.valueOf(objectId),
//						String.valueOf(subObjectId));
//			}
//		}
//	}

	private void addRelations(Iterator<?> subObjectIterator,Object objectId,
			HelperDao<?> helperDao) throws PersistentObjectExpectedException {
		while (subObjectIterator.hasNext()) {
			Object nestedObject = subObjectIterator.next();
			checkIfPersistent(nestedObject);
			Object subObjectId = getIdOfObject(nestedObject);
				helperDao.insert(String.valueOf(objectId),
						String.valueOf(subObjectId));
		}
	}

	private void checkIfPersistent(Object nestedObject)
			throws PersistentObjectExpectedException {
		DAO<?> daoForSubtype = DaoManager.getInstance().getDaoForType(
				nestedObject.getClass());
		Object persistentObject = daoForSubtype
				.load(getIdOfObject(nestedObject));
		if (persistentObject == null) {
			throw new PersistentObjectExpectedException(nestedObject);
		} else if (!ObjectComparator.areEqual(nestedObject, persistentObject)) {
			throw new PersistentObjectExpectedException(nestedObject);
		}
	}

	private Object getIdOfObject(Object object) {
		Field idField = AnnotationInterpreter.getIdField(object.getClass());
		return ValueHandler.getValueOfField(object, idField);
	}

	/**
	 * If ID of param exists in Table, this method will update the entry with
	 * the ID. Otherwise it will create a new entry.
	 * 
	 * Attention! Use only single id columns.
	 * 
	 * @param persistentObject
	 */
	public T insertOrUpdate(T persistentObject) {
		Field idField = AnnotationInterpreter.getIdField(getParameterType());
		try {
			Method getter = AnnotationInterpreter.getGetter(getParameterType()
					.getMethods(), idField);
			Object idFieldValue = getter.invoke(persistentObject);
			if (idFieldValue != null && load(idFieldValue) != null) {
				return update(persistentObject);
			}
			return insert(persistentObject);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | MethodNotFound e) {
			Log.e(DAO.class.getName(), e.getMessage());
		}
		return null;
	}

	private T update(T object) {
		insertOrUpdateChildren(object);
		try {
			database.getWriteableDb().execSQL(
					SQLBuilder.createUpdateSql(object));
		} catch (NoPersistenceClassException e) {
			Log.e("Database", e.getMessage());
		}
		return object;
	}

	/**
	 * Loads the object with the param ID from DB.
	 * 
	 * @param idFieldValue
	 * @return T object with id
	 */
	public T load(Object idFieldValue) {
		if (idFieldValue == null)
			return null;
		Field idField = AnnotationInterpreter.getIdField(getParameterType());
		String idColumn = AnnotationInterpreter.getColumnToField(idField);
		Map<String, Object> columnToValueMap = new HashMap<String, Object>();
		columnToValueMap.put(idColumn, idFieldValue);
		try {
			Cursor loaded = database.getWriteableDb().rawQuery(
					SQLBuilder.createSelectSql(columnToValueMap,
							getParameterType()), null);
			if (loaded.moveToFirst())
				return createObject(loaded);
		} catch (NoPersistenceClassException e) {
			Log.e("Database", e.getMessage());
		}
		return null;
	}

	public T createObject(Cursor loaded) {
		Map<String, String> extractedSimpleFields = ValueExtractor
				.extractToMap(loaded, getParameterType());
		String idValue = loaded.getString(loaded
				.getColumnIndex(AnnotationInterpreter
						.getIdColumn(getParameterType())));
		if (AnnotationInterpreter.hasToManyFields(getParameterType())){
			try {
				extractedSimpleFields.putAll(loadToManyRelations(idValue));
			} catch (NoPersistenceClassException npce){
				Log.e("Database", npce.getMessage());
			}
		}
		return new ObjectCreator<T>(getParameterType())
				.createNewObject(extractedSimpleFields);
	}

	private Map<String, String> loadToManyRelations(String idFieldValue)
			throws NoPersistenceClassException {
		Map<String, String> toManyIds = new HashMap<String, String>();
		List<Field> toManyFields = AnnotationInterpreter
				.getToManyFields(getParameterType());
		for (Field field : toManyFields) {
			toManyIds.put(AnnotationInterpreter.getHelperTable(
					getParameterType(), field), idFieldValue);
		}
		return toManyIds;
	}

	abstract protected Class<T> getParameterType();

	public void setDatabase(Database db) {
		database = db;
	}

}
