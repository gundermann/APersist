package com.ng.apersist;

import java.util.Iterator;
import java.util.Map;

import com.ng.apersist.interpreter.AnnotationInterpreter;

public class SQLBuilder {

	public static String createInsertSql(Object object) {
		StringBuilder builder = new StringBuilder("insert into ");
		builder.append(AnnotationInterpreter.getTable(object.getClass()))
				.append(" values (");
		builder.append(createSqlValuesPart(object));
		builder.append(");");
		return builder.toString();
	}

	public static String createUpdateSql(Object object) {
		StringBuilder builder = new StringBuilder("update ");
		builder.append(AnnotationInterpreter.getTable(object.getClass()))
				.append(" values (");
		builder.append(createSqlValuesPart(object));
		builder.append(");");
		return builder.toString();
	}

	private static String createSqlValuesPart(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	public static String createSelectSql(Map<String, Object> columnToValueMap,
			Class<?> parameterType) {
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

	public static String createMaxIdSelectionSql(Class<?> parameterType) {
		StringBuilder builder = new StringBuilder("select MAX(id) from ");
		builder.append(AnnotationInterpreter.getTable(parameterType)).append(
				";");
		return builder.toString();
	}

	public static String createCreateSql(Class<?> persistenceClass) {
		return null;
		// TODO Auto-generated method stub
		
	}

	public static String createDropSql(Class<?> persistenceClass) {
		// TODO Auto-generated method stub
		return null;
	}
}
