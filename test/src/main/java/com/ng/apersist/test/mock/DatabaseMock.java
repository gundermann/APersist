package com.ng.apersist.test.mock;

import com.ng.apersist.Database;

import org.mockito.Mockito;

public class DatabaseMock {

	public static Database create() {
		Database db = Mockito.mock(Database.class);
		return db ;
	}


}
