package com.plugtree.dm.proj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.runtime.KieContext;
import org.kie.api.runtime.process.ProcessInstance;

public class Requirement {

	public static enum Status {
		CREATED, READY, IN_DEVELOPMENT, COMPILE_ERROR, DEPLOY_ERROR, IN_TEST, BUGS_FOUND, BUGS_FIXED, COMPLETED, INVALID;   
	}
	
	private List<Assignment> developers = new ArrayList<Assignment>();
	private List<Assignment> testers = new ArrayList<Assignment>();
	private String name;
	private String description;
	private int priority;
	private Status status = Status.CREATED;
	private List<Bug> bugsFound = new ArrayList<Bug>();
	private List<Bug> bugsFixed = new ArrayList<Bug>();
	
	private String compileErrorMessage;
	private String deployErrorMessage;
	
	private boolean testingDone = false;
	private boolean developmentDone = false;
	
	public Requirement() {
	}
	
	public Requirement(String name, String description) {
		this.name = name;
		this.description = description;
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

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public List<Assignment> getDevelopers() {
		return developers;
	}

	public void setDevelopers(List<Assignment> developers) {
		this.developers = developers;
	}

	public List<Assignment> getTesters() {
		return testers;
	}

	public void setTesters(List<Assignment> testers) {
		this.testers = testers;
	}
	
	public String getLastDeveloperId() {
		if (developers.isEmpty()) {
			return null;
		}
		return developers.get(developers.size() -1).getUserId();
	}
	
	public String getLastTesterId() {
		if (testers.isEmpty()) {
			return null;
		}
		return testers.get(testers.size() -1).getUserId();
	}
	
	public void assignTester(String userId) {
		testers.add(new Assignment(userId));
	}
	
	public void finishTesting(String userId) {
		testingDone = true;
	}
	
	public void finishDevelopment(String userId) {
		developmentDone = true;
	}
	
	public boolean isDevelopmentDone() {
		return developmentDone;
	}
	
	public boolean isTestingDone() {
		return testingDone;
	}
	
	public void addDeveloper(String userId) {
		developers.add(new Assignment(userId));
	}
	
	public List<Bug> getBugsFixed() {
		return bugsFixed;
	}
	
	public List<Bug> getBugsFound() {
		return bugsFound;
	}
	
	public void setBugsFixed(List<Bug> bugsFixed) {
		this.bugsFixed = bugsFixed;
	}
	
	public void setBugsFound(List<Bug> bugsFound) {
		this.bugsFound = bugsFound;
	}

	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public void resolveBug(Bug bug) {
		bugsFound.remove(bug);
		bugsFixed.add(bug);
	}

	public void addBug(Bug bug) {
		bugsFound.add(bug);
	}
	
	public boolean hasBugs() {
		return !bugsFound.isEmpty();
	}
	
	public String getCompileErrorMessage() {
		return compileErrorMessage;
	}
	
	public String getDeployErrorMessage() {
		return deployErrorMessage;
	}
	
	public void setCompileErrorMessage(String compileErrorMessage) {
		this.compileErrorMessage = compileErrorMessage;
	}
	
	public void setDeployErrorMessage(String deployErrorMessage) {
		this.deployErrorMessage = deployErrorMessage;
	}
	
	public void setDevelopmentDone(boolean developmentDone) {
		this.developmentDone = developmentDone;
	}
	
	public void setTestingDone(boolean testingDone) {
		this.testingDone = testingDone;
	}
	
	public void determineStatus(KieContext kcontext) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("req", this);
		ProcessInstance pi = kcontext.getKieRuntime().startProcess("DMDemo.RequirementStatusSmartProcess", params);
		assert pi.getState() == ProcessInstance.STATE_COMPLETED;
	}
}
