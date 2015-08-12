package com.ng.apersist.test.dao.testpackage;

import com.ng.apersist.dao.DAO;

public class TestDaoTwo extends DAO<TestClassTwo> {

	@Override
	protected Class<TestClassTwo> getParameterType() {
		return TestClassTwo.class;
	}

}
