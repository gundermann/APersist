package com.ng.apersist.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ng.apersist.Database;
import com.ng.apersist.query.SQLBuilder;

import android.database.Cursor;

public class HelperDao<T> {

	private String table;
	private Database db;
	private String idColumn;
	private DAO<T> foreignDao;
	private String foreignIdColumn;

	public HelperDao(String table, String idColumn, String foreignIdColumn,
			Database db, DAO<T> foreignDao) {
		this.table = table;
		this.idColumn = idColumn;
		this.foreignIdColumn = foreignIdColumn;
		this.db = db;
		this.foreignDao = foreignDao;
	}

	public Collection<T> loadAll(String id) {
		Map<String, Object> columnToValueMap = fillWhereMapWithId(id);
		Cursor cursor = db.execQuery(SQLBuilder.createSelectSql(
				columnToValueMap, table));
		List<Long> foreignIds = extractCursor(cursor);
		return getForeignObjects(foreignIds);
	}

	private Collection<T> getForeignObjects(List<Long> foreignIds) {
		Collection<T> foreignObjects = new ArrayList<T>();
		for (Long id : foreignIds) {
			foreignObjects.add(foreignDao.load(id));
		}
		return foreignObjects;
	}

	private List<Long> extractCursor(Cursor cursor) {
		List<Long> ids = new ArrayList<Long>();
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				ids.add(cursor.getLong(1));
				cursor.moveToNext();
			}
		}
		return ids;
	}

	private Map<String, Object> fillWhereMapWithId(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(idColumn, id);
		return map;
	}

	public void insert(String objectId, String subObjectId) {
		db.getWriteableDb().execSQL(
				SQLBuilder.createInsertSqlForHelper(table, idColumn, objectId,
						foreignIdColumn, subObjectId));
	}

	public DAO<T> getForeignDao() {
		return foreignDao;
	}

	public void delete(String objectId, String subObjectId) {
		db.getWriteableDb().execSQL(
				SQLBuilder.createDeleteSqlForHelper(table, idColumn, objectId,
						foreignIdColumn, subObjectId));
	}

}
