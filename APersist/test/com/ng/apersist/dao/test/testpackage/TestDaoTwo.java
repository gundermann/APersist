package com.ng.apersist.dao.test.testpackage;

import com.ng.apersist.dao.DAO;
import com.ng.apersist.test.testpackage.TestClassTwo;

public class TestDaoTwo extends DAO<TestClassTwo> {

	@Override
	protected Class<TestClassTwo> getParameterType() {
		return TestClassTwo.class;
	}

}
