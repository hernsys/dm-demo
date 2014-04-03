package com.plugtree.dm.proj;

public class Bug {

	public static enum Status {
		OPEN, IN_PROGRESS, DONE, WONT_FIX, REOPENED, CLOSED;
	}
	
	private String reporterId;
	private String assigneeId;
	private String title;
	private String description;
	
	private Status status;

	public String getReporterId() {
		return reporterId;
	}

	public void setReporterId(String reporterId) {
		this.reporterId = reporterId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	public String getAssigneeId() {
		return assigneeId;
	}
	
	public void setAssigneeId(String assigneeId) {
		this.assigneeId = assigneeId;
	}
}
