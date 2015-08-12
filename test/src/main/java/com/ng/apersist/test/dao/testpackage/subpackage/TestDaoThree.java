package com.ng.apersist.test.dao.testpackage.subpackage;

import com.ng.apersist.dao.DAO;

public class TestDaoThree extends DAO<TestClassThree>{

	@Override
	protected Class<TestClassThree> getParameterType() {
		return TestClassThree.class;
	}

}
