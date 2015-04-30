package com.ng.apersist.dao.test;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ng.apersist.Database;
import com.ng.apersist.dao.test.testpackage.subpackage.TestDaoThree;

public class DaoTest {

	private static Database database;
	private static TestDaoThree dao;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
//		database = DatabaseMock.create();
		dao = new TestDaoThree();
	}

	@Test
	public void test() {
//		TestClassThree testObject = new TestClassThree();
//		testObject.setName("test");
//		testObject.setTimestamp(new Date());
//		testObject = dao.insertOrUpdate(testObject);
//		assertTrue(testObject != null);
//		assertTrue(testObject.getId() == 1L);

	}

}
