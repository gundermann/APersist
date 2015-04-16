package com.ng.apersist.util;

import java.util.HashMap;
import java.util.Map;

import android.database.Cursor;

public class ValueExtractor {

	public static Map<String, String> extractToMap(Cursor c,
			Class<?> parameterType) {
		Map<String, String> map = new HashMap<String, String>();
		String[] columnNames = c.getColumnNames();
		for (int i = 0; i < columnNames.length; i++) {
			map.put(columnNames[i],
					c.getString(c.getColumnIndex(columnNames[i])));
		}
		return map;
	}
}
