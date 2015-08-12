package com.ng.apersist.test;

import com.ng.apersist.test.dao.testpackage.TestClassTwo;
import com.ng.apersist.test.dao.testpackage.subpackage.TestClassThree;
import com.ng.apersist.util.PersistenceClassCollector;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertTrue;

public class PersistenceClassCollectorTest {

	@Test
	public void test() {
		Set<Class<?>> collectedClasses = PersistenceClassCollector.collectFromPath(this.getClass().getPackage(), null);
		assertTrue(collectedClasses.size() == 2);
		assertTrue(collectedClasses.contains(TestClassTwo.class));
		assertTrue(collectedClasses.contains(TestClassThree.class));
	}
}
