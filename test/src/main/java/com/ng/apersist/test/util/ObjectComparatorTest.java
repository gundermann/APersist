package com.ng.apersist.test.util;

import com.ng.apersist.test.dao.testpackage.subpackage.TestClassThree;
import com.ng.apersist.test.util.testpackage.TestClassOne;
import com.ng.apersist.test.util.testpackage.TestClassTwo;
import com.ng.apersist.util.ObjectComparator;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Testclass for the Object-Comparator
 *
 * Created by NG on 24.12.2016.
 */

public class ObjectComparatorTest {


    @Test
    public void testAreEqualAllObjectsNull(){
        assertTrue(ObjectComparator.areEqual(null, null));
    }

    @Test
    public void testAreEqualOneObjectNull(){
        assertFalse(ObjectComparator.areEqual(null, new Object()));
        assertFalse(ObjectComparator.areEqual(new Object(), null));
    }

    @Test
    public void testAreEqualDifferentClasses(){
        assertFalse(ObjectComparator.areEqual(new TestClassOne(), new TestClassTwo()));
    }

    @Test
    public void testAreEqualNoFields(){
        assertTrue(ObjectComparator.areEqual(new TestClassThree(), new TestClassThree()));
    }

    @Test
    public void testAreEqualDifferentIterable(){
        TestClassOne a = new TestClassOne();
        List<String> list = new ArrayList<>();
        list.add("String");
        a.setStrings(list);
        TestClassOne b = new TestClassOne();
        assertFalse(ObjectComparator.areEqual(a, b));
    }

    @Test
    public void testAreEqualDifferentSimpleValue(){
        TestClassOne a = new TestClassOne();
        a.setInteger(1);
        TestClassOne b = new TestClassOne();
        assertFalse(ObjectComparator.areEqual(a, b));
    }

    @Test
    public void testAreEqualWithEqualObjects(){
        TestClassOne a = new TestClassOne();
        a.setStrings(new ArrayList<String>());
        a.setInteger(1);
        TestClassOne b = new TestClassOne();
        b.setStrings(new ArrayList<String>());
        b.setInteger(1);
        assertTrue(ObjectComparator.areEqual(a, b));
    }

    @Test
    public void testAreEqualDifferentObjectWithNullFields(){TestClassOne a = new TestClassOne();
        a.setStrings(new ArrayList<String>());
        a.setInteger(1);
        a.setClassThree(new TestClassThree());
        TestClassOne b = new TestClassOne();
        b.setStrings(new ArrayList<String>());
        b.setInteger(1);
        assertFalse(ObjectComparator.areEqual(a, b));
    }

}
