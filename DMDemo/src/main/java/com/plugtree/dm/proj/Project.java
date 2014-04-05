package com.plugtree.dm.proj;

import java.util.ArrayList;
import java.util.List;

public class Project {

	public static enum Status {
		OPEN, CLOSED;
	}
	
	private Status status;
	
	private List<Requirement> fullBacklog = new ArrayList<Requirement>();
	
	private String name;
	
	private String description;

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<Requirement> getFullBacklog() {
		return fullBacklog;
	}

	public void setFullBacklog(List<Requirement> fullBacklog) {
		this.fullBacklog = fullBacklog;
	}

	public List<Requirement> getOpenBacklog() {
		List<Requirement> retval = new ArrayList<Requirement>();
		for (Requirement req : fullBacklog) {
			if (req.getStatus() != Requirement.Status.COMPLETED || req.getStatus() != Requirement.Status.INVALID || req.getStatus() != Requirement.Status.CREATED) {
				retval.add(req);
			}
		}
		return fullBacklog;
	}

	public List<Requirement> getClosedBacklog() {
		List<Requirement> retval = new ArrayList<Requirement>();
		for (Requirement req : fullBacklog) {
			if (req.getStatus() == Requirement.Status.COMPLETED) {
				retval.add(req);
			}
		}
		return fullBacklog;
	}
	
	public List<Requirement> getToStartBacklog() {
		List<Requirement> retval = new ArrayList<Requirement>();
		for (Requirement req : fullBacklog) {
			if (req.getStatus() == Requirement.Status.CREATED) {
				retval.add(req);
			}
		}
		return fullBacklog;
	}

	public List<Requirement> getInvalidBacklog() {
		List<Requirement> retval = new ArrayList<Requirement>();
		for (Requirement req : fullBacklog) {
			if (req.getStatus() == Requirement.Status.INVALID) {
				retval.add(req);
			}
		}
		return fullBacklog;
	}

	public boolean isClosed() {
		return Status.CLOSED == this.status;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public void fromStartToPending(Requirement req) {
		req.setStatus(Requirement.Status.READY);
	}
	
	public Requirement nextToStartRequirement() {
		return getToStartBacklog().iterator().next();
	}
	
	public void addNewRequirement(Requirement requirement) {
		requirement.setStatus(Requirement.Status.CREATED);
	}
	
	public void updateLatestStableVersion() {
		//TODO implement this method
	}
}
