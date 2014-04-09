package com.plugtree.dm.dmdemo.handlers;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

/**
 * WorkItemHandler for the Service Task "Display Error Report"
 * 
 * @author Ezequiel Grande
 *
 */
public class DisplayErrorReportHandler implements WorkItemHandler {

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub
		manager.completeWorkItem(workItem.getId(), null);
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub

	}

}