package com.plugtree.dm.dmdemo.handlers;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReconcileWithVacationBalanceHandler implements WorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(ReconcileWithVacationBalanceHandler.class);

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        /*Boolean vacationBalance = (Math.random()>0.5) ? Boolean.TRUE : Boolean.FALSE;  
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("vacationBalance", vacationBalance);
        logger.debug("== Execute: Reconcile With Vacation Balance Handler ======== vacationBalance:" + vacationBalance);
        manager.completeWorkItem(workItem.getId(), results);*/
        logger.info("== Execute: Reconcile With Vacation Balance Handler ======== ");
        manager.completeWorkItem(workItem.getId(), null);
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        logger.debug("== Abort: Reconcile With Vacation Balance Handler ========");
        manager.abortWorkItem(workItem.getId());

    }

}
