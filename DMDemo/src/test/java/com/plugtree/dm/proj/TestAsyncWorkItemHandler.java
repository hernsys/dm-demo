package com.plugtree.dm.proj;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class TestAsyncWorkItemHandler implements WorkItemHandler {

	private WorkItem workItem;

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		this.workItem = workItem;
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		this.workItem = null;
	}

	public WorkItem getWorkItem() {
		WorkItem retval = workItem;
		workItem = null;
		return retval;
	}
}
