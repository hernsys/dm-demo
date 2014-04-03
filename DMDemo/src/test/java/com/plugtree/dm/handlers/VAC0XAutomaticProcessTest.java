package com.plugtree.dm.dmdemo.handlers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
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

public class VAC0XAutomaticProcessTest {

	private KieSession ksession;
	
	@Before
	public void startUp() {
		KieContainer kcontainer = KieServices.Factory.get().getKieClasspathContainer();
		ksession = kcontainer.newKieSession();
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
	
	@After
	public void tearDown() {
		ksession.dispose();
		ksession = null;
	}
	
	@Test
	public void testVacationProcess01() {
		ProcessInstance pi = ksession.startProcess("DMDemo.vac01");
		Assert.assertNotNull(pi);
		Assert.assertEquals(ProcessInstance.STATE_COMPLETED, pi.getState());
	}

	@Test
	public void testVacationProcess02() {
		ProcessInstance pi = ksession.startProcess("DMDemo.vac02");
		Assert.assertNotNull(pi);
		Assert.assertEquals(ProcessInstance.STATE_COMPLETED, pi.getState());
	}

	@Test
	public void testVacationProcess03() {
		ProcessInstance pi = ksession.startProcess("DMDemo.vac03");
		Assert.assertNotNull(pi);
		Assert.assertEquals(ProcessInstance.STATE_COMPLETED, pi.getState());
	}

	@Test
	public void testVacationProcess04() {
		ProcessInstance pi = ksession.startProcess("DMDemo.vac04");
		Assert.assertNotNull(pi);
		Assert.assertEquals(ProcessInstance.STATE_COMPLETED, pi.getState());
	}

	@Test
	public void testVacationProcess05() {
		ProcessInstance pi = ksession.startProcess("DMDemo.vac05");
		Assert.assertNotNull(pi);
		Assert.assertEquals(ProcessInstance.STATE_COMPLETED, pi.getState());
	}

	@Test
	public void testVacationProcess06() {
		ProcessInstance pi = ksession.startProcess("DMDemo.vac06");
		Assert.assertNotNull(pi);
		Assert.assertEquals(ProcessInstance.STATE_COMPLETED, pi.getState());
	}
}
