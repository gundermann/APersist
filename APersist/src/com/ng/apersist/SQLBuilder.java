package com.ng.apersist;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ng.apersist.interpreter.AnnotationInterpreter;
import com.ng.apersist.util.NoPersistenceClassException;
import com.ng.apersist.util.ValueHandler;

public class SQLBuilder {

	public static String createInsertSql(Object object)
			throws NoPersistenceClassException {
		StringBuilder builder = new StringBuilder("insert into ");
		builder.append(AnnotationInterpreter.getTable(object.getClass()))
				.append(" set ");
		builder.append(createSqlValuesPart(object));
		builder.append(";");
		return builder.toString();
	}

	public static String createUpdateSql(Object object)
			throws NoPersistenceClassException {
		StringBuilder builder = new StringBuilder("update ");
		builder.append(AnnotationInterpreter.getTable(object.getClass()))
				.append(" values (");
		builder.append(createSqlValuesPart(object));
		builder.append(");");
		return builder.toString();
	}

	private static String createSqlValuesPart(Object object) {
		StringBuilder sb = new StringBuilder();
		List<Field> allColumnFields = AnnotationInterpreter
				.getAllColumnFields(object.getClass());
		for (Iterator<Field> iterator = allColumnFields.iterator(); iterator
				.hasNext();) {
			Field field = iterator.next();
			String column = AnnotationInterpreter.getColumnToField(field);
			String value;
			if (AnnotationInterpreter.isSimpleField(field)) {
				value = ValueHandler.getDatabaseTypeAsStringFromField(object,
						field);
			} else {
				Object nestedObject = ValueHandler.getValueOfField(object,
						field);
				Field idField = AnnotationInterpreter.getIdField(nestedObject
						.getClass());
				value = ValueHandler.getDatabaseTypeAsStringFromField(
						nestedObject, idField);
			}
			sb.append(column).append(" = ").append(value);
			if (iterator.hasNext())
				sb.append(", ");
		}
		return sb.toString();
	}

	public static String createSelectSql(Map<String, Object> columnToValueMap,
			Class<?> parameterType) throws NoPersistenceClassException {
		StringBuilder builder = new StringBuilder("select * from ");
		if (!columnToValueMap.keySet().isEmpty()) {
			builder.append(AnnotationInterpreter.getTable(parameterType))
					.append(" where ");
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
			sb.append(column).append(" = ")
					.append(columnToValueMap.get(column));
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
			Class<?> databaseType = ValueHandler.getDatabaseTypeFor(field
					.getType());
			sb.append(field.getName()).append(" ")
					.append(databaseType.getSimpleName());
			sb.append(specificFieldDescription(field));
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
		} else if (AnnotationInterpreter.isForeignKey(field)) {
			try {
				sb.append(" references ")
						.append(AnnotationInterpreter.getTable(field.getType()))
						.append(" (")
						.append(AnnotationInterpreter.getTargetField(field))
						.append(")");
			} catch (NoPersistenceClassException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static String createDropSql(Class<?> persistenceClass) {
		StringBuilder sb = new StringBuilder("drop table if exists ");
		try {
			sb.append(AnnotationInterpreter.getTable(persistenceClass)).append(
					";");
		} catch (NoPersistenceClassException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
