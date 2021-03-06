package com.plugtree.dm.dmdemo.handlers;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WorkItemHandler for the Service Task "Display Error Report"
 * 
 * @author Ezequiel Grande
 *
 */
public class DisplayErrorReportHandler implements WorkItemHandler {
	private Logger logger = LoggerFactory.getLogger(DisplayErrorReportHandler.class);
	
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		logger.info("====> Displaying error report...");
		manager.completeWorkItem(workItem.getId(), null);
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub

	}

}
