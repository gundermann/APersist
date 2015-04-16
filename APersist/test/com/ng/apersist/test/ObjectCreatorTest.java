package com.ng.apersist.test;

import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import android.annotation.SuppressLint;

import com.ng.apersist.ObjectCreator;
import com.ng.apersist.test.mock.ColumnToValueMapCreator;
import com.ng.apersist.test.testpackage.subpackage.TestClassThree;

@SuppressLint("SimpleDateFormat")
public class ObjectCreatorTest{

	private static ObjectCreator<TestClassThree> objectCreator;
	private static Map<String, String> map;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		map = ColumnToValueMapCreator.forTestClassTwo();
		objectCreator = new ObjectCreator<TestClassThree>(TestClassThree.class);
	}


	@Test
	public void testObjectWithoutForeignKey() {
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

}
