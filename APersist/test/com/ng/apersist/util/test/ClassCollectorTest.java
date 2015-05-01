package com.ng.apersist.util.test;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import com.ng.apersist.util.ClassCollector;

public class ClassCollectorTest {


	@Test
	public void test() {
		Set<String> collectedClasses = ClassCollector.collectFromPath(this.getClass().getPackage(), null);
		assertTrue(collectedClasses.size() == 4);
	}

}
