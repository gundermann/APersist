package com.ng.apersist.dao.test;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import com.ng.apersist.dao.DAO;
import com.ng.apersist.dao.DaoCollector;
import com.ng.apersist.dao.test.testpackage.TestDaoTwo;
import com.ng.apersist.dao.test.testpackage.subpackage.TestDaoThree;

public class DaoCollectorTest {


	@Test
	public void test() {
		Set<Class<DAO<?>>> collectFromPath = DaoCollector.collectFromPath(this.getClass().getPackage(), null);
		assertTrue(collectFromPath.size() == 2);
		assertTrue(collectFromPath.contains(TestDaoTwo.class));
		assertTrue(collectFromPath.contains(TestDaoThree.class));
	}

}
