package com.plugtree.dm.proj;

import java.util.Date;

public class Assignment {

	private String userId;
	private Date assignmentDate;

	public Assignment(String userId) {
		this.userId = userId;
		this.assignmentDate = new Date();
	}

	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public Date getAssignmentDate() {
		return assignmentDate;
	}
	
	public void setAssignmentDate(Date assignmentDate) {
		this.assignmentDate = assignmentDate;
	}
}
