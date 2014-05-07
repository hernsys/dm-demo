package com.plugtree.dm.dmdemo.handlers;

import org.kie.api.runtime.process.WorkItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class FillVacationRequestHandler implements WorkItemHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(FillVacationRequestHandler.class);

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
	    logger.debug("== Execute: Fill Vacation Request Handler ========"); 
		manager.completeWorkItem(workItem.getId(), null);
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
	    logger.debug("== Abort: Fill Vacation Request Handler ========");
		manager.abortWorkItem(workItem.getId());
	}

}
