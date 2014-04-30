package com.plugtree.dm.dmdemo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.drools.core.util.DateUtils;
import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plugtree.dm.dmdemo.LeaveRequest.Type;
import com.plugtree.dm.dmdemo.service.AbsenceService;

/**
 * Test Cases for the Approval Rules
 * 
 * @author Ezequiel Grande
 * 
 */
public class ApprovalRulesTest {
	private Logger logger = LoggerFactory.getLogger(ApprovalRulesTest.class);
	private static final String SELECT_APPROVERS_RFG = "select-approvers";
	private static final String VACATION_DRL = "vacation.drl";
	private static final String MOCK_PROCESS_ID = "com.plugtree.dm.mock.approvers";
	private static RuntimeManager manager;

	@BeforeClass
	public static void init() {
		System.setProperty("drools.dateformat", "yyyyMMdd");
		manager = createRuntimeManager();
	}

	@Test
	public void testPresidentRequest() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		final List<String> firedRules = new ArrayList<String>();
		RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
		final KieSession ksession = getConfiguredSession(engine, firedRules);

		// President requests Marriage Leave without payment
		Employee president = createPresident(id);
		LeaveRequest presidentReq = createPresidentLeave(president);

		// Add Globals
		Employee hrvp = createHRVP(id, president);
		ksession.setGlobal("hrvp", hrvp);

		// Start the Process, which triggers the ruleflow-group select-approvers
		startMockProcess(ksession, presidentReq);

		// Assert that one rule was fired
		Assert.assertEquals(1, firedRules.size());

		// Assert President rule was triggered, the HR VP will approve his leave
		Assert.assertEquals(1, presidentReq.getApprovals().size());
		Assert.assertEquals(hrvp, presidentReq.getApprovals().get(0)
				.getApprover());

		// Dispose session
		manager.disposeRuntimeEngine(engine);
	}

	@Test
	public void testExecutiveRequest() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		final List<String> firedRules = new ArrayList<String>();
		RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
		final KieSession ksession = getConfiguredSession(engine, firedRules);

		// Create the President of the Company
		Employee president = createPresident(id);

		// CEO requests Paternity Leave without payment
		Employee ceo = createCEO(id, "NameOfVP_1", president);
		LeaveRequest ceoReq = createCEORequest(ceo);

		// Add Globals
		Employee hrvp = createHRVP(id, president);
		ksession.setGlobal("hrvp", hrvp);

		// Start the Process, which triggers the ruleflow-group select-approvers
		startMockProcess(ksession, ceoReq);

		// Assert that two rule were fired
		Assert.assertEquals(2, firedRules.size());

		// Assert CEO rule was triggered, the President will approve his leave
		Assert.assertEquals(1, ceoReq.getApprovals().size());
		Assert.assertEquals(president, ceoReq.getApprovals().get(0)
				.getApprover());

		// Dispose session
		manager.disposeRuntimeEngine(engine);
	}

	@Test
	public void testAnnualWithOutSalary() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		final List<String> firedRules = new ArrayList<String>();
		RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
		final KieSession ksession = getConfiguredSession(engine, firedRules);

		// Create Company hierarchy
		Employee president = createPresident(id);
		Employee ceo = createCEO(id, "NameOfVP_1", president);
		Employee operator = createOperator(id, ceo);

		// Operator requests Annual Leave without payment
		LeaveRequest operatorRequest = createOperatorRequestBuilder(operator,
				LeaveType.ANNUAL, false).build();

		// Add Globals
		ksession.setGlobal("vacationDepartment",
				CompensationDepartment.VACATION);

		// Start the Process, which triggers the ruleflow-group select-approvers
		startMockProcess(ksession, operatorRequest);

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());
		assertSupervisorIsApprover(operatorRequest, operator);

		// Assert that the Vacation Department will review his request
		Assert.assertFalse(operatorRequest.getCompensationDepartments()
				.isEmpty());
		Assert.assertEquals(1, operatorRequest.getCompensationDepartments()
				.size());
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.VACATION));

		// Dispose session
		manager.disposeRuntimeEngine(engine);
	}

	@Test
	public void testAnnualWithSalary() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		final List<String> firedRules = new ArrayList<String>();
		RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
		final KieSession ksession = getConfiguredSession(engine, firedRules);

		// Create Company hierarchy
		Employee president = createPresident(id);
		Employee ceo = createCEO(id, "NameOfVP_1", president);
		Employee operator = createOperator(id, ceo);

		// Operator requests Annual Leave with payment
		LeaveRequest operatorRequest = createOperatorRequestBuilder(operator,
				LeaveType.ANNUAL, true).build();

		// Add Globals
		ksession.setGlobal("payrollDepartment", CompensationDepartment.PAYROLL);
		ksession.setGlobal("vacationDepartment",
				CompensationDepartment.VACATION);
		ksession.setGlobal("travelDepartment", CompensationDepartment.TRAVEL);

		// Start the Process, which triggers the ruleflow-group select-approvers
		startMockProcess(ksession, operatorRequest);

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());

		// Assert that his supervisor will approve the request
		assertSupervisorIsApprover(operatorRequest, operator);

		// Assert that the Vacation, Travel and Payroll Departments will review
		// his request
		Assert.assertFalse(operatorRequest.getCompensationDepartments()
				.isEmpty());
		Assert.assertEquals(3, operatorRequest.getCompensationDepartments()
				.size());
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.VACATION));
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.TRAVEL));
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.PAYROLL));

		// Dispose session
		manager.disposeRuntimeEngine(engine);
	}

	@Test
	public void testUpdateAnnualWithOutSalary() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		final List<String> firedRules = new ArrayList<String>();
		RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
		final KieSession ksession = getConfiguredSession(engine, firedRules);

		// Create Company hierarchy
		Employee president = createPresident(id);
		Employee ceo = createCEO(id, "NameOfVP_1", president);
		Employee operator = createOperator(id, ceo);

		// Operator updates Annual Leave without payment
		LeaveRequest operatorRequest = createOperatorRequestBuilder(operator,
				LeaveType.ANNUAL, false).type(Type.MODIFY).build();

		// Add Globals
		ksession.setGlobal("vacationDepartment",
				CompensationDepartment.VACATION);
		ksession.setGlobal("travelDepartment", CompensationDepartment.TRAVEL);

		// Start the Process, which triggers the ruleflow-group select-approvers
		startMockProcess(ksession, operatorRequest);

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());
		assertSupervisorIsApprover(operatorRequest, operator);

		// Assert that the Vacation and Travel Departments will review his
		// request
		Assert.assertFalse(operatorRequest.getCompensationDepartments()
				.isEmpty());
		Assert.assertEquals(2, operatorRequest.getCompensationDepartments()
				.size());
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.VACATION));
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.TRAVEL));

		// Dispose session
		manager.disposeRuntimeEngine(engine);
	}

	@Test
	public void testUpdateAnnualWithSalary() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		final List<String> firedRules = new ArrayList<String>();
		RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
		final KieSession ksession = getConfiguredSession(engine, firedRules);

		// Create Company hierarchy
		Employee president = createPresident(id);
		Employee ceo = createCEO(id, "NameOfVP_1", president);
		Employee operator = createOperator(id, ceo);

		// Operator updates Annual Leave with payment
		LeaveRequest operatorRequest = createOperatorRequestBuilder(operator,
				LeaveType.ANNUAL, true).type(Type.MODIFY).build();

		// Add Globals
		ksession.setGlobal("payrollDepartment", CompensationDepartment.PAYROLL);
		ksession.setGlobal("vacationDepartment",
				CompensationDepartment.VACATION);
		ksession.setGlobal("travelDepartment", CompensationDepartment.TRAVEL);

		// Start the Process, which triggers the ruleflow-group select-approvers
		startMockProcess(ksession, operatorRequest);

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());

		// Assert that his supervisor will approve the request
		assertSupervisorIsApprover(operatorRequest, operator);

		// Assert that the Vacation, Travel and Payroll Departments will review
		// his request
		Assert.assertFalse(operatorRequest.getCompensationDepartments()
				.isEmpty());
		Assert.assertEquals(3, operatorRequest.getCompensationDepartments()
				.size());
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.VACATION));
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.TRAVEL));
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.PAYROLL));

		// Dispose session
		manager.disposeRuntimeEngine(engine);
	}

	@Test
	public void testCancelAnnualWithOutSalary() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		final List<String> firedRules = new ArrayList<String>();
		RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
		final KieSession ksession = getConfiguredSession(engine, firedRules);

		// Create Company hierarchy
		Employee president = createPresident(id);
		Employee ceo = createCEO(id, "NameOfVP_1", president);
		Employee operator = createOperator(id, ceo);

		// Operator cancels Annual Leave without payment
		LeaveRequest operatorRequest = createOperatorRequestBuilder(operator,
				LeaveType.ANNUAL, false).type(Type.CANCEL).build();

		// Add Globals
		ksession.setGlobal("vacationDepartment",
				CompensationDepartment.VACATION);
		ksession.setGlobal("travelDepartment", CompensationDepartment.TRAVEL);

		// Start the Process, which triggers the ruleflow-group select-approvers
		startMockProcess(ksession, operatorRequest);

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());
		assertSupervisorIsApprover(operatorRequest, operator);

		// Assert that the Vacation and Travel Departments will review his
		// request
		Assert.assertFalse(operatorRequest.getCompensationDepartments()
				.isEmpty());
		Assert.assertEquals(2, operatorRequest.getCompensationDepartments()
				.size());
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.VACATION));
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.TRAVEL));

		// Dispose session
		manager.disposeRuntimeEngine(engine);
	}

	@Test
	public void testCancelAnnualWithSalary() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		final List<String> firedRules = new ArrayList<String>();
		RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
		final KieSession ksession = getConfiguredSession(engine, firedRules);

		// Create Company hierarchy
		Employee president = createPresident(id);
		Employee ceo = createCEO(id, "NameOfVP_1", president);
		Employee operator = createOperator(id, ceo);

		// Operator cancels an Annual Leave with payment
		LeaveRequest operatorRequest = createOperatorRequestBuilder(operator,
				LeaveType.ANNUAL, true).type(Type.CANCEL).build();

		// Add Globals
		ksession.setGlobal("payrollDepartment", CompensationDepartment.PAYROLL);
		ksession.setGlobal("vacationDepartment",
				CompensationDepartment.VACATION);
		ksession.setGlobal("travelDepartment", CompensationDepartment.TRAVEL);

		// Start the Process, which triggers the ruleflow-group select-approvers
		startMockProcess(ksession, operatorRequest);

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());

		// Assert that his supervisor will approve the request
		assertSupervisorIsApprover(operatorRequest, operator);

		// Assert that the Vacation, Travel and Payroll Departments will review
		// his request
		Assert.assertFalse(operatorRequest.getCompensationDepartments()
				.isEmpty());
		Assert.assertEquals(3, operatorRequest.getCompensationDepartments()
				.size());
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.VACATION));
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.TRAVEL));
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.PAYROLL));

		// Dispose session
		manager.disposeRuntimeEngine(engine);
	}

	@Test
	public void testEducationWithoutSalary() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		final List<String> firedRules = new ArrayList<String>();
		RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
		final KieSession ksession = getConfiguredSession(engine, firedRules);

		// Create Company hierarchy
		Employee president = createPresident(id);
		Employee ceo = createCEO(id, "NameOfVP_1", president);
		Employee vp = createVicePresident(id, "NameOfVP_1", ceo);
		Employee gm = createGeneralManager(id, "NameOfGM_1_1", vp);
		Employee sm = createSectionManager(id, "NameOfSM_A", gm);
		Employee operator = createOperator(id, sm);

		// Operator requests Education without salary Leave
		LeaveRequest operatorRequest = createOperatorRequestBuilder(operator,
				LeaveType.EDUCATION, false).build();

		// Add Globals
		Employee hrvp = createHRVP(id, president);
		ksession.setGlobal("hrvp", hrvp);
		ksession.setGlobal("vacationDepartment",
				CompensationDepartment.VACATION);

		// Start the Process, which triggers the ruleflow-group select-approvers
		startMockProcess(ksession, operatorRequest);

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());

		// Assert approvers: his supervisor, the VP of his area and the HR VP
		Assert.assertEquals(3, operatorRequest.getApprovals().size());
		assertSupervisorIsApprover(operatorRequest, operator);
		Assert.assertTrue(isApprover(operatorRequest, vp));
		Assert.assertTrue(isApprover(operatorRequest, hrvp));

		// Assert that the Vacation Department will review his request
		Assert.assertFalse(operatorRequest.getCompensationDepartments()
				.isEmpty());
		Assert.assertEquals(1, operatorRequest.getCompensationDepartments()
				.size());
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.VACATION));

		// Dispose session
		manager.disposeRuntimeEngine(engine);
	}

	@Test
	public void testExam() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		final List<String> firedRules = new ArrayList<String>();
		RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
		final KieSession ksession = getConfiguredSession(engine, firedRules);

		// Create Company hierarchy
		Employee president = createPresident(id);
		Employee ceo = createCEO(id, "NameOfVP_1", president);
		Employee vp = createVicePresident(id, "NameOfVP_1", ceo);
		Employee gm = createGeneralManager(id, "NameOfGM_1_1", vp);
		Employee sm = createSectionManager(id, "NameOfSM_A", gm);
		Employee operator = createOperator(id, sm);

		// Operator requests Exam leave
		LeaveRequest operatorRequest = createOperatorRequestBuilder(operator,
				LeaveType.EXAM, false).build();

		// Add Globals
		ksession.setGlobal("vacationDepartment",
				CompensationDepartment.VACATION);

		// Start the Process, which triggers the ruleflow-group select-approvers
		startMockProcess(ksession, operatorRequest);

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());

		// Assert approvers: his supervisor
		Assert.assertEquals(1, operatorRequest.getApprovals().size());
		assertSupervisorIsApprover(operatorRequest, operator);

		// Assert that the Vacation Department will review his request
		Assert.assertFalse(operatorRequest.getCompensationDepartments()
				.isEmpty());
		Assert.assertEquals(1, operatorRequest.getCompensationDepartments()
				.size());
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.VACATION));

		// Dispose session
		manager.disposeRuntimeEngine(engine);
	}

	@Test
	public void testExceptionalLessThanTwoMonths() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		final List<String> firedRules = new ArrayList<String>();
		RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
		final KieSession ksession = getConfiguredSession(engine, firedRules);

		// Create Company hierarchy
		Employee president = createPresident(id);
		Employee ceo = createCEO(id, "NameOfVP_1", president);
		Employee vp = createVicePresident(id, "NameOfVP_1", ceo);
		Employee gm = createGeneralManager(id, "NameOfGM_1_1", vp);
		Employee sm = createSectionManager(id, "NameOfSM_A", gm);
		Employee operator = createOperator(id, sm);

		// Operator requests Exceptional leave less or equals than 2 months
		LeaveRequest operatorRequest = createOperatorRequestBuilder(operator,
				LeaveType.EXCEPTIONAL, false)
				.plannedStartDate(DateUtils.parseDate("20140101"))
				.plannedEndDate(DateUtils.parseDate("20140302")).build();

		logger.debug(" ==> Days of exceptional leave: "
				+ AbsenceService.getAbsenceDays(operator,
						operatorRequest.getPlannedStartDate(),
						operatorRequest.getPlannedEndDate()));

		// Add Globals
		ksession.setGlobal("vacationDepartment",
				CompensationDepartment.VACATION);
		ksession.setGlobal("payrollDepartment", CompensationDepartment.PAYROLL);

		// Start the Process, which triggers the ruleflow-group select-approvers
		startMockProcess(ksession, operatorRequest);

		// Assert duration, less than two months
		Assert.assertTrue(AbsenceService.getAbsenceDays(operator,
				operatorRequest.getPlannedStartDate(),
				operatorRequest.getPlannedEndDate()) <= 60);

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());

		// Assert approvers: his supervisor and GM of his area
		Assert.assertEquals(2, operatorRequest.getApprovals().size());
		assertSupervisorIsApprover(operatorRequest, operator);
		Assert.assertTrue("General Manager has not been set as Approver",
				isApprover(operatorRequest, gm));

		// Assert that the Vacation and Payroll Departments will review his
		// request
		Assert.assertFalse(operatorRequest.getCompensationDepartments()
				.isEmpty());
		Assert.assertEquals(2, operatorRequest.getCompensationDepartments()
				.size());
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.VACATION));
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.PAYROLL));

		// Dispose session
		manager.disposeRuntimeEngine(engine);
	}

	@Test
	public void testExceptionalMoreThanTwoMonths() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		final List<String> firedRules = new ArrayList<String>();
		RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
		final KieSession ksession = getConfiguredSession(engine, firedRules);

		// Create Company hierarchy
		Employee president = createPresident(id);
		Employee ceo = createCEO(id, "NameOfVP_1", president);
		Employee vp = createVicePresident(id, "NameOfVP_1", ceo);
		Employee gm = createGeneralManager(id, "NameOfGM_1_1", vp);
		Employee sm = createSectionManager(id, "NameOfSM_A", gm);
		Employee operator = createOperator(id, sm);

		// Operator requests Exceptional leave more than 2 months
		LeaveRequest operatorRequest = createOperatorRequestBuilder(operator,
				LeaveType.EXCEPTIONAL, false)
				.plannedStartDate(DateUtils.parseDate("20140101"))
				.plannedEndDate(DateUtils.parseDate("20140303")).build();

		logger.debug(" ==> Days of exceptional leave: "
				+ AbsenceService.getAbsenceDays(operator,
						operatorRequest.getPlannedStartDate(),
						operatorRequest.getPlannedEndDate()));

		// Add Globals
		ksession.setGlobal("vacationDepartment",
				CompensationDepartment.VACATION);
		ksession.setGlobal("payrollDepartment", CompensationDepartment.PAYROLL);

		// Start the Process, which triggers the ruleflow-group select-approvers
		startMockProcess(ksession, operatorRequest);

		// Assert duration, more than two months
		Assert.assertTrue(AbsenceService.getAbsenceDays(operator,
				operatorRequest.getPlannedStartDate(),
				operatorRequest.getPlannedEndDate()) > 60);

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());

		// Assert approvers: his supervisor, GM and VP of his area
		Assert.assertEquals(3, operatorRequest.getApprovals().size());
		assertSupervisorIsApprover(operatorRequest, operator);
		Assert.assertTrue(isApprover(operatorRequest, gm));
		Assert.assertTrue(isApprover(operatorRequest, vp));

		// Assert that the Vacation and Payroll Departments will review his
		// request
		Assert.assertFalse(operatorRequest.getCompensationDepartments()
				.isEmpty());
		Assert.assertEquals(2, operatorRequest.getCompensationDepartments()
				.size());
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.VACATION));
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.PAYROLL));

		// Dispose session
		manager.disposeRuntimeEngine(engine);
	}

	@Test
	public void testMarriage() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		final List<String> firedRules = new ArrayList<String>();
		RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
		final KieSession ksession = getConfiguredSession(engine, firedRules);

		// Create Company hierarchy
		Employee president = createPresident(id);
		Employee ceo = createCEO(id, "NameOfVP_1", president);
		Employee vp = createVicePresident(id, "NameOfVP_1", ceo);
		Employee gm = createGeneralManager(id, "NameOfGM_1_1", vp);
		Employee sm = createSectionManager(id, "NameOfSM_A", gm);
		Employee operator = createOperator(id, sm);

		// Operator requests Marriage leave
		LeaveRequest operatorRequest = createOperatorRequestBuilder(operator,
				LeaveType.MARRIAGE, false).build();

		// Add Globals
		ksession.setGlobal("vacationDepartment",
				CompensationDepartment.VACATION);

		// Start the Process, which triggers the ruleflow-group select-approvers
		startMockProcess(ksession, operatorRequest);

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());

		// Assert approvers: his supervisor
		Assert.assertEquals(1, operatorRequest.getApprovals().size());
		assertSupervisorIsApprover(operatorRequest, operator);

		// Assert that the Vacation Department will review his request
		Assert.assertFalse(operatorRequest.getCompensationDepartments()
				.isEmpty());
		Assert.assertEquals(1, operatorRequest.getCompensationDepartments()
				.size());
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.VACATION));

		// Dispose session
		manager.disposeRuntimeEngine(engine);
	}

	@Test
	public void testPaternity() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		final List<String> firedRules = new ArrayList<String>();
		RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
		final KieSession ksession = getConfiguredSession(engine, firedRules);

		// Create Company hierarchy
		Employee president = createPresident(id);
		Employee ceo = createCEO(id, "NameOfVP_1", president);
		Employee vp = createVicePresident(id, "NameOfVP_1", ceo);
		Employee gm = createGeneralManager(id, "NameOfGM_1_1", vp);
		Employee sm = createSectionManager(id, "NameOfSM_A", gm);
		Employee operator = createOperator(id, sm);

		// Operator requests Paternity leave
		LeaveRequest operatorRequest = createOperatorRequestBuilder(operator,
				LeaveType.PATERNITY, false).build();

		// Add Globals
		ksession.setGlobal("vacationDepartment",
				CompensationDepartment.VACATION);

		// Start the Process, which triggers the ruleflow-group select-approvers
		startMockProcess(ksession, operatorRequest);

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());

		// Assert approvers: his supervisor
		Assert.assertEquals(1, operatorRequest.getApprovals().size());
		assertSupervisorIsApprover(operatorRequest, operator);

		// Assert that the Vacation Department will review his request
		Assert.assertFalse(operatorRequest.getCompensationDepartments()
				.isEmpty());
		Assert.assertEquals(1, operatorRequest.getCompensationDepartments()
				.size());
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.VACATION));

		// Dispose session
		manager.disposeRuntimeEngine(engine);
	}

	@Test
	public void testTransfer() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		final List<String> firedRules = new ArrayList<String>();
		RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
		final KieSession ksession = getConfiguredSession(engine, firedRules);

		// Create Company hierarchy
		Employee president = createPresident(id);
		Employee ceo = createCEO(id, "NameOfVP_1", president);
		Employee vp = createVicePresident(id, "NameOfVP_1", ceo);
		Employee gm = createGeneralManager(id, "NameOfGM_1_1", vp);
		Employee sm = createSectionManager(id, "NameOfSM_A", gm);
		Employee operator = createOperator(id, sm);

		// Operator requests Transfer leave
		LeaveRequest operatorRequest = createOperatorRequestBuilder(operator,
				LeaveType.TRANSFER, false).build();

		// Add Globals
		ksession.setGlobal("vacationDepartment",
				CompensationDepartment.VACATION);

		// Start the Process, which triggers the ruleflow-group select-approvers
		startMockProcess(ksession, operatorRequest);

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());

		// Assert approvers: his supervisor
		Assert.assertEquals(1, operatorRequest.getApprovals().size());
		assertSupervisorIsApprover(operatorRequest, operator);

		// Assert that the Vacation Department will review his request
		Assert.assertFalse(operatorRequest.getCompensationDepartments()
				.isEmpty());
		Assert.assertEquals(1, operatorRequest.getCompensationDepartments()
				.size());
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.VACATION));

		// Dispose session
		manager.disposeRuntimeEngine(engine);
	}

	@Test
	public void testEmployeeGetSupervisor() {
		AtomicInteger id = new AtomicInteger();

		// Create Company hierarchy
		Employee president = createPresident(id);
		Employee ceo = createCEO(id, "NameOfVP_1", president);
		Employee operator = createOperator(id, ceo);

		Assert.assertEquals(ceo, operator.getSupervisor(Role.EXECUTIVE));
		Assert.assertEquals(president, operator.getSupervisor(Role.PRESIDENT));
		Assert.assertNull(operator.getSupervisor(Role.GENERAL_MANAGER));
	}

	/**
	 * Asserts if the Direct Supervisor of the employee is an approver of the
	 * LeaveRequest
	 * 
	 * @param leaveRequest
	 * @param employee
	 */
	private void assertSupervisorIsApprover(LeaveRequest leaveRequest,
			Employee employee) {
		Assert.assertFalse(leaveRequest.getApprovals().isEmpty());
		Assert.assertTrue("Direct Supervisor has not been set as Approver",
				isApprover(leaveRequest, employee.getDirectSupervisor()));
	}

	/**
	 * Returns true if the LeaveRequest has a LeaveApproval which must be
	 * approved by the approver sent as parameter
	 * 
	 * @param leaveRequest
	 * @param approver
	 * @return true if the approver is an approver for the LeaveRequest
	 */
	private boolean isApprover(LeaveRequest leaveRequest, Employee approver) {
		boolean isApprover = false;
		Assert.assertFalse(leaveRequest.getApprovals().isEmpty());
		for (LeaveApproval approval : leaveRequest.getApprovals()) {
			if (approver.equals(approval.getApprover())) {
				isApprover = true;
				break;
			}
		}
		return isApprover;
	}

	/**
	 * Returns a Resources representing a Mock Process, which has only one Rule
	 * Set Node for the ruleflow-group: "select-approvers".
	 * 
	 * @return a mock process for testing purposes
	 */
	private static Resource getMockProcessAsResource() {
		RuleFlowProcessFactory factory = RuleFlowProcessFactory
				.createProcess(MOCK_PROCESS_ID);
		factory
		// Header
		.name("ApprovalRulesTestProcess")
				.version("1.0")
				.packageName("com.plugtree")
				// Nodes
				.startNode(1).name("Start").done().ruleSetNode(2)
				.name("Action").ruleFlowGroup(SELECT_APPROVERS_RFG).done()
				.endNode(3).name("End").done()
				// Connections
				.connection(1, 2).connection(2, 3);
		RuleFlowProcess process = factory.validate().getProcess();

		return ResourceFactory
				.newByteArrayResource(XmlBPMNProcessDumper.INSTANCE.dump(
						process).getBytes());
	}

	/**
	 * Returns a new RuntimeManager to be shared by the test cases
	 * 
	 * @return a new RuntimeManager
	 */
	private static RuntimeManager createRuntimeManager() {
		// Environment Configuration
		RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory
				.get()
				.newEmptyBuilder()
				.addAsset(getMockProcessAsResource(), ResourceType.BPMN2)
				.addAsset(ResourceFactory.newClassPathResource(VACATION_DRL),
						ResourceType.DRL).get();
		return RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(
				environment);
	}

	/**
	 * Retrieves the KieSession from the Engine and configures the Listeners and
	 * Globals needed by the Test Cases. Finally, returns the KieSession.
	 * 
	 * @param engine
	 * @param firedRules
	 * @return the Configured KieSession
	 */
	private KieSession getConfiguredSession(RuntimeEngine engine,
			final Collection<String> firedRules) {
		final KieSession ksession = engine.getKieSession();
		ksession.addEventListener(new DefaultAgendaEventListener() {
			@Override
			public void afterMatchFired(AfterMatchFiredEvent event) {
				firedRules.add(event.getMatch().getRule().getName());
			}

			@Override
			public void matchCreated(MatchCreatedEvent event) {
				logger.debug(">> MATCH has been created: "
						+ event.getMatch().getRule().getName());
				ksession.fireAllRules();
			}

			@Override
			public void afterRuleFlowGroupActivated(
					RuleFlowGroupActivatedEvent event) {
				logger.debug(">> RULEFLOW-GROUP has been activated: "
						+ event.getRuleFlowGroup().getName());
				ksession.fireAllRules();
			}
		});
		// Required for logging
		ksession.setGlobal("logger", logger);
		return ksession;
	}

	/**
	 * Creates and starts the Mock Process, setting the LeaveRequest as a
	 * Process Variable and adding the additional variables required by the
	 * Business Rules. Asserts that the Process has been completed.
	 * 
	 * @param ksession
	 * @param leaveRequest
	 */
	private void startMockProcess(final KieSession ksession,
			LeaveRequest leaveRequest) {
		logger.debug("=== Starting mock process ===");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("leaveRequest", leaveRequest);
		params.put("approvals", leaveRequest.getApprovals());
		WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession
				.createProcessInstance(MOCK_PROCESS_ID, params);
		Assert.assertEquals(ProcessInstance.STATE_PENDING,
				processInstance.getState());
		ksession.insert(processInstance);
		ksession.startProcessInstance(processInstance.getId());
		Assert.assertEquals(ProcessInstance.STATE_COMPLETED,
				processInstance.getState());
	}

	/*
	 * METHODS FOR CREATING THE EMPLOYEES HIERARCHY AND LEAVE REQUESTS
	 */

	private LeaveRequest.Builder createOperatorRequestBuilder(
			Employee operator, LeaveType leaveType, Boolean requestPayment) {
		return new LeaveRequest.Builder(operator, leaveType, requestPayment)
				.absenceReason(AbsenceReason.HOLIDAY).comments(
						"My very good reasons");
	}

	private Employee createOperator(AtomicInteger id, Employee directSupervisor) {
		return new Employee.Builder(id.getAndIncrement(), "John")
				.basicSalary(BigDecimal.valueOf(1000)).email("john@mail.com")

				.jobStartDate(DateUtils.parseDate("19990101"))
				.phone("555-555-555").position("Developer").role(Role.OPERATOR)
				.directSupervisor(directSupervisor).build();
	}

	private LeaveRequest createCEORequest(Employee ceo) {
		return new LeaveRequest.Builder(ceo, LeaveType.ANNUAL, false)
				.absenceReason(AbsenceReason.OTHER)
				.comments("I have a footbal game").build();
	}

	private Employee createCEO(AtomicInteger id, String name,
			Employee directSupervisor) {
		return new Employee.Builder(id.getAndIncrement(), name)
				.basicSalary(BigDecimal.valueOf(15000)).email("jim@mail.com")

				.jobStartDate(DateUtils.parseDate("19990101"))
				.phone("555-555-555").position("CEO").role(Role.EXECUTIVE)
				.directSupervisor(directSupervisor).build();
	}

	private Employee createVicePresident(AtomicInteger id, String name,
			Employee directSupervisor) {
		return new Employee.Builder(id.getAndIncrement(), name)
				.basicSalary(BigDecimal.valueOf(15000))
				.email(name + "@mail.com")
				.jobStartDate(DateUtils.parseDate("19990101"))
				.phone("555-555-555").position("VP").role(Role.VICE_PRESIDENT)
				.directSupervisor(directSupervisor).build();
	}

	private Employee createGeneralManager(AtomicInteger id, String name,
			Employee directSupervisor) {
		return new Employee.Builder(id.getAndIncrement(), name)
				.basicSalary(BigDecimal.valueOf(15000))
				.email(name + "@mail.com")
				.jobStartDate(DateUtils.parseDate("19990101"))
				.phone("555-555-555").position("GM").role(Role.GENERAL_MANAGER)
				.directSupervisor(directSupervisor).build();
	}

	private Employee createSectionManager(AtomicInteger id, String name,
			Employee directSupervisor) {
		return new Employee.Builder(id.getAndIncrement(), name)
				.basicSalary(BigDecimal.valueOf(15000))
				.email(name + "@mail.com")
				.jobStartDate(DateUtils.parseDate("19990101"))
				.phone("555-555-555").position("SM").role(Role.SECTION_MANAGER)
				.directSupervisor(directSupervisor).build();
	}

	private LeaveRequest createPresidentLeave(Employee president) {
		return new LeaveRequest.Builder(president, LeaveType.MARRIAGE, false)
				.absenceReason(AbsenceReason.OTHER)
				.comments("I'm getting married").build();
	}

	private Employee createPresident(AtomicInteger id) {
		return new Employee.Builder(id.getAndIncrement(), "Peter")
				.basicSalary(BigDecimal.valueOf(50000)).email("peter@mail.com")
				.jobStartDate(DateUtils.parseDate("19990101"))
				.phone("555-555-555").position("President")
				.role(Role.PRESIDENT).build();
	}

	private Employee createHRVP(AtomicInteger id, Employee directSupervisor) {
		return new Employee.Builder(id.getAndIncrement(), "HR VP")
				.basicSalary(BigDecimal.valueOf(15000)).email("hr@mail.com")
				.jobStartDate(DateUtils.parseDate("19990101"))
				.phone("555-555-555").position("HR VP")
				.role(Role.VICE_PRESIDENT).directSupervisor(directSupervisor)
				.build();
	}
}
