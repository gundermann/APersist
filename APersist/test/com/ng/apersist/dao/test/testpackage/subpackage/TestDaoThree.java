package com.ng.apersist.dao.test.testpackage.subpackage;

import com.ng.apersist.dao.DAO;
import com.ng.apersist.test.testpackage.subpackage.TestClassThree;

public class TestDaoThree extends DAO<TestClassThree>{

	@Override
	protected Class<TestClassThree> getParameterType() {
		return TestClassThree.class;
	}

}
