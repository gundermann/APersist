package com.ng.apersist.test.mock;

import java.util.HashMap;
import java.util.Map;

public class ColumnToValueMapCreator {

	public static Map<String, String> forTestClassTwo() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", "1");
		map.put("name", "testname");
		map.put("timestamp", "01.01.2015-00:00:00");
		return map ;
	}

	public static Map<String, String> forTestClassTwoWithNull() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", null);
		map.put("name", null);
		map.put("timestamp", null);
		return map ;
		}

}
