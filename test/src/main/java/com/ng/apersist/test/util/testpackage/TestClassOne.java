package com.ng.apersist.test.util.testpackage;

import com.ng.apersist.annotation.Column;
import com.ng.apersist.annotation.ToOne;
import com.ng.apersist.test.dao.testpackage.subpackage.TestClassThree;

import java.util.List;

public class TestClassOne {

    @Column
    private Integer integer;

    @Column
    private List<String> strings;

    @ToOne
    private TestClassThree classThree;

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }

    public List<String> getStrings() {
        return strings;
    }

    public void setStrings(List<String> strings) {
        this.strings = strings;
    }

    public TestClassThree getClassThree() {
        return classThree;
    }

    public void setClassThree(TestClassThree classThree) {
        this.classThree = classThree;
    }
}
