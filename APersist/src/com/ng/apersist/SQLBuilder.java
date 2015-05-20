package com.ng.apersist;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ng.apersist.annotation.ToMany;
import com.ng.apersist.interpreter.AnnotationInterpreter;
import com.ng.apersist.util.NoPersistenceClassException;
import com.ng.apersist.util.ValueHandler;

public class SQLBuilder {

	public static String createInsertSql(Object object)
			throws NoPersistenceClassException {
		StringBuilder builder = new StringBuilder("insert into ");
		builder.append(AnnotationInterpreter.getTable(object.getClass()));
		builder.append(createSqlValuesPart(object));
		builder.append(";");
		return builder.toString();
	}

	public static String createDeleteSql(Object object)
			throws NoPersistenceClassException {
		StringBuilder sb = new StringBuilder("delete from ");
		sb.append(AnnotationInterpreter.getTable(object.getClass())).append(
				" where ");
		Map<String, Object> columnToValueMap = new HashMap<String, Object>();
		Field idField = AnnotationInterpreter.getIdField(object.getClass());
		Object value = ValueHandler.getValueOfField(object, idField);
		columnToValueMap.put(AnnotationInterpreter.getColumnToField(idField),
				value);
		sb.append(createWhereCondition(columnToValueMap));
		sb.append(";");
		return sb.toString();
	}

	public static String createUpdateSql(Object object)
			throws NoPersistenceClassException {
		StringBuilder builder = new StringBuilder("update ");
		builder.append(AnnotationInterpreter.getTable(object.getClass()));
		builder.append(createSqlValuesUpdatePart(object));
		builder.append(";");
		return builder.toString();
	}

	private static String createSqlValuesUpdatePart(Object object) {
		StringBuilder sb = new StringBuilder(" set  ");
		List<Field> allColumnFields = AnnotationInterpreter
				.getAllColumnFields(object.getClass());
		for (Iterator<Field> iterator = allColumnFields.iterator(); iterator
				.hasNext();) {
			Field field = iterator.next();
			String column = AnnotationInterpreter.getColumnToField(field);
			String value;
			if (AnnotationInterpreter.isSimpleField(field)) {
				value = ValueHandler.getDatabaseTypeAsSQLValueFromField(object,
						field);
			} else {
				Object nestedObject = ValueHandler.getValueOfField(object,
						field);
				if (nestedObject != null) {
					Field idField = AnnotationInterpreter
							.getIdField(nestedObject.getClass());
					value = ValueHandler.getDatabaseTypeAsSQLValueFromField(
							nestedObject, idField);
				} else {
					value = null;
				}
			}

			sb.append(column).append(" = ").append(value);
			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	private static String createSqlValuesPart(Object object) {
		StringBuilder sb = new StringBuilder(" (");
		StringBuilder valuesSb = new StringBuilder(" values (");
		List<Field> allColumnFields = AnnotationInterpreter
				.getAllColumnFields(object.getClass());
		for (Iterator<Field> iterator = allColumnFields.iterator(); iterator
				.hasNext();) {
			Field field = iterator.next();
			String column = AnnotationInterpreter.getColumnToField(field);
			String value;
			if (AnnotationInterpreter.isSimpleField(field)) {
				value = ValueHandler.getDatabaseTypeAsSQLValueFromField(object,
						field);
			} else {
				Object nestedObject = ValueHandler.getValueOfField(object,
						field);
				if (nestedObject != null) {
					Field idField = AnnotationInterpreter
							.getIdField(nestedObject.getClass());
					value = ValueHandler.getDatabaseTypeAsSQLValueFromField(
							nestedObject, idField);
				} else {
					value = null;
				}
			}

			sb.append(column);
			valuesSb.append(value);
			if (iterator.hasNext()) {
				sb.append(", ");
				valuesSb.append(", ");
			}
		}
		valuesSb.append(")");
		return sb.append(") ").append(valuesSb).toString();
	}

	public static String createSelectSql(Map<String, Object> columnToValueMap,
			Class<?> parameterType) throws NoPersistenceClassException {
		StringBuilder builder = new StringBuilder("select * from ");
		builder.append(AnnotationInterpreter.getTable(parameterType));
		if (columnToValueMap != null && !columnToValueMap.keySet().isEmpty()) {
			builder.append(" where ");
			builder.append(createWhereCondition(columnToValueMap));
		}
		builder.append(";");
		return builder.toString();

	}

	private static String createWhereCondition(
			Map<String, Object> columnToValueMap) {
		StringBuilder sb = new StringBuilder();
		for (Iterator<String> iterator = columnToValueMap.keySet().iterator(); iterator
				.hasNext();) {
			String column = iterator.next();
			sb.append(column)
					.append(" = ")
					.append(ValueHandler
							.convertDatabaseTypeToString(columnToValueMap
									.get(column)));
			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	public static String createMaxIdSelectionSql(Class<?> parameterType)
			throws NoPersistenceClassException {
		StringBuilder builder = new StringBuilder("select MAX(id) from ");
		builder.append(AnnotationInterpreter.getTable(parameterType)).append(
				";");
		return builder.toString();
	}

	public static String createCreateSql(Class<?> persistenceClass)
			throws NoPersistenceClassException {
		StringBuilder sb = new StringBuilder("create table ");
		sb.append(AnnotationInterpreter.getTable(persistenceClass))
				.append(" (");
		List<Field> allColumnFields = AnnotationInterpreter
				.getAllColumnFields(persistenceClass);
		for (Iterator<Field> iterator = allColumnFields.iterator(); iterator
				.hasNext();) {
			Field field = iterator.next();
			if (!AnnotationInterpreter.isToMany(field)) {
				Class<?> databaseType = ValueHandler.getDatabaseTypeFor(field
						.getType());
				sb.append(field.getName()).append(" ")
						.append(databaseType.getSimpleName());
				sb.append(specificFieldDescription(field));
			}
			if (iterator.hasNext())
				sb.append(",");
		}
		sb.append(");");
		return sb.toString();
	}

	private static String specificFieldDescription(Field field) {
		StringBuilder sb = new StringBuilder();

		if (AnnotationInterpreter.isIdField(field)) {
			sb.append(" primary key");
			if (AnnotationInterpreter.isAutoincrement(field)) {
				sb.append(" autoincrement");
			}
		} else if (AnnotationInterpreter.isToOne(field)) {
			try {
				sb.append(" references ")
						.append(AnnotationInterpreter.getTable(field.getType()))
						.append(" (")
						.append(AnnotationInterpreter.getTargetField(field))
						.append(")");
				if (AnnotationInterpreter.isMinOne(field)) {
					sb.append(" not null");
				}
			} catch (NoPersistenceClassException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static String createDropSql(Class<?> persistenceClass) throws NoPersistenceClassException {
		StringBuilder sb = new StringBuilder("drop table if exists ");
			sb.append(AnnotationInterpreter.getTable(persistenceClass)).append(
					";");
		return sb.toString();
	}

	public static List<String> createMainTablesCreationSqls(
			Set<Class<?>> classes) throws NoPersistenceClassException {
		List<String> sqls = new ArrayList<String>();
		for (Class<?> persistenceClass : classes) {
			sqls.add(createCreateSql(persistenceClass));
		}
		return sqls;
	}

	public static List<String> createHelperTablesCreationSqls(
			Set<Class<?>> classes) throws NoPersistenceClassException {
		List<String> sqls = new ArrayList<String>();
		for (Class<?> persistenceClass : classes) {
			sqls.addAll(createHelperCreateSql(persistenceClass));
		}
		return sqls;
	}

	private static List<String> createHelperCreateSql(Class<?> persistenceClass)
			throws NoPersistenceClassException {
		List<String> sqls = new ArrayList<String>();
		List<Field> allToManyFields = AnnotationInterpreter.getToManyFields(persistenceClass);
		for (Iterator<Field> iterator = allToManyFields.iterator(); iterator
				.hasNext();) {
			Field field = iterator.next();
				StringBuilder sb = new StringBuilder("create table ");
				ToMany annotation = AnnotationInterpreter
						.getToManyAnnotation(field);
				sb.append(AnnotationInterpreter.getTable(persistenceClass))
						.append("2")
						.append(AnnotationInterpreter.getTable(annotation
								.target())).append("( ");
				sb.append("id integer primary key, ")
						.append(AnnotationInterpreter
								.getTable(persistenceClass))
						.append("_id")
						.append(" integer ")
						.append("references ")
						.append(AnnotationInterpreter
								.getTable(persistenceClass))
						.append(" (")
						.append(AnnotationInterpreter
								.getIdColumn(persistenceClass))
						.append(")")
						.append(", ")
						.append(AnnotationInterpreter.getTable(annotation
								.target()))
						.append("_id")
						.append(" integer ")
						.append("references ")
						.append(AnnotationInterpreter.getTable(annotation
								.target()))
						.append(" (")
						.append(AnnotationInterpreter.getIdColumn(annotation
								.target())).append(")");

				sb.append(");");
				sqls.add(sb.toString());
		}

		return sqls;
	}

	public static List<String> createHelperTablesDropSqls(Set<Class<?>> classes)
			throws NoPersistenceClassException {
		List<String> sqls = new ArrayList<String>();
		for (Class<?> persistenceClass : classes) {
			sqls.addAll(createHelperDropSql(persistenceClass));
		}
		return sqls;
	}

	public static List<String> createMainTablesDropSqls(Set<Class<?>> classes)
			throws NoPersistenceClassException {
		List<String> sqls = new ArrayList<String>();
		for (Class<?> persistenceClass : classes) {
			sqls.add(createDropSql(persistenceClass));
		}
		return sqls;
	}

	private static List<String> createHelperDropSql(Class<?> persistenceClass)
			throws NoPersistenceClassException {
		List<String> helperDropSqls = new ArrayList<String>();
		List<Field> allColumnFields = AnnotationInterpreter
				.getAllColumnFields(persistenceClass);
		for (Iterator<Field> iterator = allColumnFields.iterator(); iterator
				.hasNext();) {
			Field field = iterator.next();
			if (AnnotationInterpreter.isToMany(field)) {
				ToMany annotation = AnnotationInterpreter.getToManyAnnotation(field);
				StringBuilder sb = new StringBuilder("drop table if exists ");
				sb.append(AnnotationInterpreter.getTable(persistenceClass))
						.append("2")
						.append(AnnotationInterpreter.getTable(annotation.target()))
						.append(";");
				helperDropSqls.add(sb.toString());
			}
		}
		return helperDropSqls;
	}

	public static String createSelectSql(Map<String, Object> columnToValueMap,
			String table) {
		StringBuilder builder = new StringBuilder("select * from ");
		builder.append(table);
		if (columnToValueMap != null && !columnToValueMap.keySet().isEmpty()) {
			builder.append(" where ");
			builder.append(createWhereCondition(columnToValueMap));
		}
		builder.append(";");
		return builder.toString();
	}
}
