package com.plugtree.dm.dmdemo;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemHandler;

import com.plugtree.dm.dmdemo.handlers.ApprovalNotificationHandler;
import com.plugtree.dm.dmdemo.handlers.FillVacationRequestHandler;
import com.plugtree.dm.dmdemo.handlers.ReconcileWithVacationBalanceHandler;
import com.plugtree.dm.dmdemo.handlers.RejectionNotificationHandler;
import com.plugtree.dm.dmdemo.handlers.ReviewAndApproveForTicketHandler;
import com.plugtree.dm.dmdemo.handlers.ReviewAndApproveForVacationHandler;
import com.plugtree.dm.dmdemo.handlers.ReviewAndApproveHandler;
import com.plugtree.dm.dmdemo.handlers.ReviewAndApproveSalarySubmittedHandler;
import com.plugtree.dm.dmdemo.handlers.ReviewAndApproveThePayrollHandler;
import com.plugtree.util.KieTestHelper;

public class VAC0XAutomaticProcessTest {

    private KieSession ksession;

    private static final KieContainer kcontainer = KieTestHelper.createKieContainer("vac01.bpmn2", "vac02.bpmn2",
            "vac03.bpmn2", "vac04.bpmn2", "vac05.bpmn2", "vac06.bpmn2");

    @Before
    public void startUp() {
        ksession = KieTestHelper.createKieSession(kcontainer);
        this.registerWorkItemHandlers();
    }

    @After
    public void tearDown() {
        ksession.dispose();
        ksession = null;
    }

    public void testVacationProcess01_Travel_HRDirector() {

    }

    @Test
    public void testProcess01_Balance_Review_Approval() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("vacationBalance", Boolean.TRUE);
        params.put("reviewAndApprove", ApprovalType.APPROVED);
        params.put("approvalType", ApprovalType.APPROVED);

        ProcessInstance pi = ksession.startProcess("DMDemo.vac01", params);
        Assert.assertNotNull(pi);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, pi.getState());
    }

    @Test
    public void testProcess01_Balance_Rejected_Approval() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("vacationBalance", Boolean.TRUE);
        params.put("reviewAndApprove", ApprovalType.REJECTED);
        params.put("approvalType", ApprovalType.APPROVED);

        ProcessInstance pi = ksession.startProcess("DMDemo.vac01", params);
        Assert.assertNotNull(pi);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, pi.getState());
    }

    @Test
    public void testProcess02_Balance_Review_Approval() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("vacationBalance", Boolean.TRUE);
        params.put("reviewAndApprove", ApprovalType.APPROVED);
        params.put("approvalType", ApprovalType.APPROVED);

        ProcessInstance pi = ksession.startProcess("DMDemo.vac02", params);
        Assert.assertNotNull(pi);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, pi.getState());
    }

    @Test
    public void testProcess02_Balance_Rejected_Approval() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("vacationBalance", Boolean.TRUE);
        params.put("reviewAndApprove", ApprovalType.REJECTED);
        params.put("approvalType", ApprovalType.APPROVED);

        ProcessInstance pi = ksession.startProcess("DMDemo.vac02", params);
        Assert.assertNotNull(pi);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, pi.getState());
    }

    @Test
    public void testProcess03_Balance_Review_Approval() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("vacationBalance", Boolean.TRUE);
        params.put("reviewAndApprove", ApprovalType.APPROVED);
        params.put("approvalType", ApprovalType.APPROVED);

        ProcessInstance pi = ksession.startProcess("DMDemo.vac03", params);
        Assert.assertNotNull(pi);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, pi.getState());
    }

    @Test
    public void testProcess03_Balance_Rejected_Approval() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("vacationBalance", Boolean.TRUE);
        params.put("reviewAndApprove", ApprovalType.REJECTED);
        params.put("approvalType", ApprovalType.APPROVED);

        ProcessInstance pi = ksession.startProcess("DMDemo.vac03", params);
        Assert.assertNotNull(pi);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, pi.getState());
    }

    @Test
    public void testProcess04_Balance_Review_Approval() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("vacationBalance", Boolean.TRUE);
        params.put("reviewAndApprove", ApprovalType.APPROVED);
        params.put("approvalType", ApprovalType.APPROVED);
        ProcessInstance pi = ksession.startProcess("DMDemo.vac04", params);
        Assert.assertNotNull(pi);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, pi.getState());
    }

    @Test
    public void testProcess04_Balance_Rejected_Approval() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("vacationBalance", Boolean.TRUE);
        params.put("reviewAndApprove", ApprovalType.REJECTED);
        params.put("approvalType", ApprovalType.APPROVED);
        ProcessInstance pi = ksession.startProcess("DMDemo.vac04", params);
        Assert.assertNotNull(pi);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, pi.getState());
    }

    @Test
    public void testProcess05_Balance_Review_Approval() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("vacationBalance", Boolean.TRUE);
        params.put("reviewAndApprove", ApprovalType.APPROVED);
        params.put("approvalType", ApprovalType.APPROVED);
        ProcessInstance pi = ksession.startProcess("DMDemo.vac05", params);
        Assert.assertNotNull(pi);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, pi.getState());
    }

    @Test
    public void testProcess05_Balance_Rejected_Approval() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("vacationBalance", Boolean.TRUE);
        params.put("reviewAndApprove", ApprovalType.REJECTED);
        params.put("approvalType", ApprovalType.APPROVED);
        ProcessInstance pi = ksession.startProcess("DMDemo.vac05", params);
        Assert.assertNotNull(pi);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, pi.getState());
    }

    @Test
    public void testProcess06_Balance_Review_Approval() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("vacationBalance", Boolean.TRUE);
        params.put("reviewAndApprove", ApprovalType.APPROVED);
        params.put("approvalType", ApprovalType.APPROVED);
        ProcessInstance pi = ksession.startProcess("DMDemo.vac06", params);
        Assert.assertNotNull(pi);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, pi.getState());
    }

    @Test
    public void testProcess06_Balance_Rejected_Approval() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("vacationBalance", Boolean.TRUE);
        params.put("reviewAndApprove", ApprovalType.REJECTED);
        params.put("approvalType", ApprovalType.APPROVED);
        ProcessInstance pi = ksession.startProcess("DMDemo.vac06", params);
        Assert.assertNotNull(pi);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, pi.getState());
    }
    
    public void registerWorkItemHandlers(){
        WorkItemHandler fvrHandler = new FillVacationRequestHandler();
        WorkItemHandler rwvbHandler = new ReconcileWithVacationBalanceHandler();
        WorkItemHandler raaHandler = new ReviewAndApproveHandler();
        WorkItemHandler raafvHandler = new ReviewAndApproveForVacationHandler();
        WorkItemHandler anHandler = new ApprovalNotificationHandler();
        WorkItemHandler rnHandler = new RejectionNotificationHandler();
        WorkItemHandler raatpHandler = new ReviewAndApproveThePayrollHandler();
        WorkItemHandler raaftHandler = new ReviewAndApproveForTicketHandler();
        WorkItemHandler raasHandler = new ReviewAndApproveSalarySubmittedHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.1-VAC-01", fvrHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.2-VAC-01", fvrHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.3-VAC-01", fvrHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.4-VAC-01", fvrHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.5-VAC-01", fvrHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.6-VAC-01", fvrHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.1-VAC-02", rwvbHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.2-VAC-02", rwvbHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.3-VAC-02", rwvbHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.4-VAC-02", rwvbHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.5-VAC-02", rwvbHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.6-VAC-02", rwvbHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.1-VAC-03", raaHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.2-VAC-03", raaHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.3-VAC-03", raaHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.4-VAC-03", raaHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.5-VAC-03", raaHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.6-VAC-03", raaHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.1-VAC-04", raafvHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.2-VAC-04", raafvHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.3-VAC-04", raafvHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.4-VAC-04", raafvHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.5-VAC-05", raafvHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.6-VAC-05", raafvHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.1-VAC-05", anHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.2-VAC-05", anHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.3-VAC-05", anHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.4-VAC-05", anHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.5-VAC-07", anHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.6-VAC-07", anHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.1-VAC-06", rnHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.2-VAC-06", rnHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.3-VAC-06", rnHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.4-VAC-06", rnHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.5-VAC-06", rnHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.6-VAC-06", rnHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.2-VAC-07", raatpHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.3-VAC-07", raaftHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.5-VAC-04", raaftHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.6-VAC-04", raaftHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.4-VAC-08", raaftHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.4-VAC-07", raasHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("VP-3.2.5-VAC-07", raasHandler);
    }

}
