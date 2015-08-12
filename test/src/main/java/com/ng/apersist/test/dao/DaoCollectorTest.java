package com.ng.apersist.test.dao;

import com.ng.apersist.dao.DAO;
import com.ng.apersist.dao.DaoCollector;
import com.ng.apersist.test.dao.testpackage.TestDaoTwo;
import com.ng.apersist.test.dao.testpackage.subpackage.TestDaoThree;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertTrue;

public class DaoCollectorTest {


	@Test
	public void test() {
		Set<Class<DAO<?>>> collectFromPath = DaoCollector.collectFromPath(this.getClass().getPackage(), null);
		assertTrue(collectFromPath.size() == 2);
		assertTrue(collectFromPath.contains(TestDaoTwo.class));
		assertTrue(collectFromPath.contains(TestDaoThree.class));
	}

}
