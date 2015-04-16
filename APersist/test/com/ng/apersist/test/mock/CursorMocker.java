package com.ng.apersist.test.mock;

import org.mockito.Mockito;

import android.test.mock.MockCursor;

public class CursorMocker {

	public static MockCursor forTestClassTwo() {
		MockCursor mock = Mockito.mock(MockCursor.class);
		Mockito.when(mock.getColumnIndex("id")).thenReturn(0);
		Mockito.when(mock.getColumnIndex("name")).thenReturn(1);
		Mockito.when(mock.getColumnIndex("timestamp")).thenReturn(2);
		
		Mockito.when(mock.getLong(0)).thenReturn(1L);
		Mockito.when(mock.getString(1)).thenReturn("testname");
		Mockito.when(mock.getString(2)).thenReturn("01.01.2015-00:00:00");
		return mock;
	}

}
