package com.ng.apersist.test;

import android.annotation.SuppressLint;

import com.ng.apersist.test.mock.ColumnToValueMapCreator;
import com.ng.apersist.test.dao.testpackage.subpackage.TestClassThree;
import com.ng.apersist.util.ObjectCreator;

import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@SuppressLint("SimpleDateFormat")
public class ObjectCreatorTest {

	private static ObjectCreator<TestClassThree> objectCreator;
	private static Map<String, String> map;

	@Test
	public void testObjectWithoutForeignKey() {
		map = ColumnToValueMapCreator.forTestClassTwo();
		objectCreator = new ObjectCreator<TestClassThree>(TestClassThree.class);
		TestClassThree object = objectCreator.createNewObject(map);
		assertTrue(object.getId() == 1L);
		assertTrue(object.getName().equals("testname"));
		try {
			DateFormat format = new SimpleDateFormat("dd.MM.yyyy-hh:mm:ss");
			Date date = format.parse("01.01.2015-00:00:00");
			assertTrue(object.getTimestamp().equals(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testObjectWithNullValues() {
		map = ColumnToValueMapCreator.forTestClassTwoWithNull();
		objectCreator = new ObjectCreator<TestClassThree>(TestClassThree.class);
		TestClassThree object = objectCreator.createNewObject(map);
		assertTrue(object.getId() == null);
		assertTrue(object.getName() == null);
		assertTrue(object.getTimestamp() == null);
	}

}
