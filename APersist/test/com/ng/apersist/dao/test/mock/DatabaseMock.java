package com.ng.apersist.dao.test.mock;

import org.mockito.Mockito;

import com.ng.apersist.Database;

public class DatabaseMock {

	public static Database create() {
		Database db = Mockito.mock(Database.class);
		return db ;
	}


}
