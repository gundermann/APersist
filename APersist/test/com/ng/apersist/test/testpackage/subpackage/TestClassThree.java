package com.ng.apersist.test.testpackage.subpackage;

import java.util.Date;

import com.ng.apersist.annotation.Column;
import com.ng.apersist.annotation.Id;
import com.ng.apersist.annotation.PersistenceClass;

@PersistenceClass
public class TestClassThree {

	
	@Id
	private Long id;
	
	@Column
	private String name;
	
	@Column
	private Date timestamp;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	
	
}
