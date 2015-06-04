package com.ng.apersist.test;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import com.ng.apersist.test.testpackage.TestClassTwo;
import com.ng.apersist.test.testpackage.subpackage.TestClassThree;
import com.ng.apersist.util.PersistenceClassCollector;

public class PersistenceClassCollectorTest {

	@Test
	public void test() {
		Set<Class<?>> collectedClasses = PersistenceClassCollector.collectFromPath(this.getClass().getPackage(), null);
		assertTrue(collectedClasses.size() == 2);
		assertTrue(collectedClasses.contains(TestClassTwo.class));
		assertTrue(collectedClasses.contains(TestClassThree.class));
	}
}
