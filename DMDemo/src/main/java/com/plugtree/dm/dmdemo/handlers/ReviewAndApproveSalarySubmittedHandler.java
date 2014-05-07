package com.plugtree.dm.dmdemo.handlers;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReviewAndApproveSalarySubmittedHandler implements WorkItemHandler {

	private static final Logger logger = LoggerFactory.getLogger(ReviewAndApproveSalarySubmittedHandler.class);

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
	    logger.debug("== Execute: Review And Approve Salary Submitted Handler ========");
		manager.completeWorkItem(workItem.getId(), null);
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		logger.debug("== Abort: Review And Approve Salary Submitted Handler ========");
        manager.abortWorkItem(workItem.getId());

	}

}
