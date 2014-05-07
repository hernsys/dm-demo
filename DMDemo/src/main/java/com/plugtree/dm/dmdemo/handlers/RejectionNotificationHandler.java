package com.plugtree.dm.dmdemo.handlers;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RejectionNotificationHandler implements WorkItemHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(RejectionNotificationHandler.class);

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
	    logger.debug("== Execute: Rejection Notification Handler ========");
		manager.completeWorkItem(workItem.getId(), null);
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
	    logger.debug("== Abort: Rejection Notification Handler ========");
        manager.abortWorkItem(workItem.getId());

	}

}
