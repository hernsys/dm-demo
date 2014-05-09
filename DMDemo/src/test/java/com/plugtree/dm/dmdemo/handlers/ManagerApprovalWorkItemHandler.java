package com.plugtree.dm.dmdemo.handlers;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plugtree.dm.entities.ApprovalType;
import com.plugtree.dm.entities.LeaveApproval;

public class ManagerApprovalWorkItemHandler  implements WorkItemHandler {
	private Logger logger = LoggerFactory.getLogger(ManagerApprovalWorkItemHandler.class);
	private WorkItem workItem;

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		logger.debug("--> [ USER TASK ] Manager Approval <--");
		this.workItem = workItem;
		LeaveApproval approval = (LeaveApproval) workItem.getParameter("in_approval");
		approval.setType(ApprovalType.ROLLBACK_MANAGER);
		Map<String, Object> results = workItem.getResults();
		if (results == null) {
			results = new HashMap<String, Object>();
		}
		results.put("out_approval", approval);
		manager.completeWorkItem(workItem.getId(), results);
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
