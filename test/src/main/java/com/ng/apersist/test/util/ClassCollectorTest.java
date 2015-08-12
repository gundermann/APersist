package com.ng.apersist.test.util;

import com.ng.apersist.util.ClassCollector;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertTrue;

public class ClassCollectorTest {


	@Test
	public void test() {
		Set<String> collectedClasses = ClassCollector.collectFromPath(this.getClass().getPackage(), null);
		assertTrue(collectedClasses.size() == 4);
	}

}
