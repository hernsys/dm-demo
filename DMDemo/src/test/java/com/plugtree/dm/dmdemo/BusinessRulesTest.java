package com.plugtree.dm.dmdemo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.drools.core.util.DateUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.KieSession;

import com.plugtree.dm.dmdemo.LeaveRequest.Type;
import com.plugtree.dm.dmdemo.service.AbsenceService;
import com.plugtree.util.KieTestHelper;

public class BusinessRulesTest {

	@BeforeClass
	public static void init() {
		System.setProperty("drools.dateformat", "yyyyMMdd");
	}

	@Test
	public void testPresidentAndExecutiveRequests() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		List<String> firedRules = new ArrayList<String>();
		KieSession ksession = this
				.createKieSession("approvers.drl", firedRules);

		// President requests Marriage Leave without payment
		Employee president = createPresident(id);
		LeaveRequest presidentReq = createPresidentLeave(president);

		// CEO requests Paternity Leave without payment
		Employee ceo = createCEO(id, "NameOfVP_1", president);
		LeaveRequest ceoReq = createCEORequest(ceo);

		// Add Globals
		Employee hrvp = createHRVP(id, president);
		ksession.setGlobal("hrvp", hrvp);

		// Add facts to session
		KieTestHelper.insert(ksession, presidentReq, ceoReq);

		// Fire rules
		System.out.println("=== FIRING RULES ===");
		ksession.fireAllRules();

		// Assert that two rules were fired
		Assert.assertEquals(3, firedRules.size());

		// Assert President rule was triggered, the HR VP will approve his leave
		Assert.assertEquals(1, presidentReq.getApprovers().size());
		Assert.assertEquals(hrvp, presidentReq.getApprovers().get(0));

		// Assert CEO rule was triggered, the President will approve his leave
		Assert.assertEquals(1, ceoReq.getApprovers().size());
		Assert.assertEquals(president, ceoReq.getApprovers().get(0));

		// Dispose session
		ksession.dispose();

	}

	@Test
	public void testAnnualWithOutSalary() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		List<String> firedRules = new ArrayList<String>();
		KieSession ksession = this
				.createKieSession("approvers.drl", firedRules);

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

		// Add facts to session
		KieTestHelper.insert(ksession, operatorRequest);

		// Fire rules
		System.out.println("=== FIRING RULES ===");
		ksession.fireAllRules();

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());
		assertSupervisorIsApprover(operator, operatorRequest);

		// Assert that the Vacation Department will review his request
		Assert.assertFalse(operatorRequest.getCompensationDepartments()
				.isEmpty());
		Assert.assertEquals(1, operatorRequest.getCompensationDepartments()
				.size());
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.VACATION));

		// Dispose session
		ksession.dispose();

	}

	@Test
	public void testAnnualWithSalary() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		List<String> firedRules = new ArrayList<String>();
		KieSession ksession = this
				.createKieSession("approvers.drl", firedRules);

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

		// Add facts to session
		KieTestHelper.insert(ksession, operatorRequest);

		// Fire rules
		System.out.println("=== FIRING RULES ===");
		ksession.fireAllRules();

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());

		// Assert that his supervisor will approve the request
		assertSupervisorIsApprover(operator, operatorRequest);

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
		ksession.dispose();

	}

	@Test
	public void testUpdateAnnualWithOutSalary() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		List<String> firedRules = new ArrayList<String>();
		KieSession ksession = this
				.createKieSession("approvers.drl", firedRules);

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

		// Add facts to session
		KieTestHelper.insert(ksession, operatorRequest);

		// Fire rules
		System.out.println("=== FIRING RULES ===");
		ksession.fireAllRules();

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());
		assertSupervisorIsApprover(operator, operatorRequest);

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
		ksession.dispose();

	}

	@Test
	public void testUpdateAnnualWithSalary() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		List<String> firedRules = new ArrayList<String>();
		KieSession ksession = this
				.createKieSession("approvers.drl", firedRules);

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

		// Add facts to session
		KieTestHelper.insert(ksession, operatorRequest);

		// Fire rules
		System.out.println("=== FIRING RULES ===");
		ksession.fireAllRules();

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());

		// Assert that his supervisor will approve the request
		assertSupervisorIsApprover(operator, operatorRequest);

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
		ksession.dispose();

	}

	@Test
	public void testCancelAnnualWithOutSalary() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		List<String> firedRules = new ArrayList<String>();
		KieSession ksession = this
				.createKieSession("approvers.drl", firedRules);

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

		// Add facts to session
		KieTestHelper.insert(ksession, operatorRequest);

		// Fire rules
		System.out.println("=== FIRING RULES ===");
		ksession.fireAllRules();

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());
		assertSupervisorIsApprover(operator, operatorRequest);

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
		ksession.dispose();

	}

	@Test
	public void testCancelAnnualWithSalary() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		List<String> firedRules = new ArrayList<String>();
		KieSession ksession = this
				.createKieSession("approvers.drl", firedRules);

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

		// Add facts to session
		KieTestHelper.insert(ksession, operatorRequest);

		// Fire rules
		System.out.println("=== FIRING RULES ===");
		ksession.fireAllRules();

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());

		// Assert that his supervisor will approve the request
		assertSupervisorIsApprover(operator, operatorRequest);

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
		ksession.dispose();

	}

	@Test
	public void testEducationWithoutSalary() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		List<String> firedRules = new ArrayList<String>();
		KieSession ksession = this
				.createKieSession("approvers.drl", firedRules);

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

		// Add facts to session
		KieTestHelper.insert(ksession, operatorRequest);

		// Fire rules
		System.out.println("=== FIRING RULES ===");
		ksession.fireAllRules();

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());

		// Assert approvers: his supervisor, the VP of his area and the HR VP
		Assert.assertEquals(3, operatorRequest.getApprovers().size());
		assertSupervisorIsApprover(operator, operatorRequest);
		Assert.assertTrue(operatorRequest.getApprovers().contains(vp));
		Assert.assertTrue(operatorRequest.getApprovers().contains(hrvp));

		// Assert that the Vacation Department will review his request
		Assert.assertFalse(operatorRequest.getCompensationDepartments()
				.isEmpty());
		Assert.assertEquals(1, operatorRequest.getCompensationDepartments()
				.size());
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.VACATION));

		// Dispose session
		ksession.dispose();

	}

	@Test
	public void testExam() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		List<String> firedRules = new ArrayList<String>();
		KieSession ksession = this
				.createKieSession("approvers.drl", firedRules);

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

		// Add facts to session
		KieTestHelper.insert(ksession, operatorRequest);

		// Fire rules
		System.out.println("=== FIRING RULES ===");
		ksession.fireAllRules();

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());

		// Assert approvers: his supervisor
		Assert.assertEquals(1, operatorRequest.getApprovers().size());
		assertSupervisorIsApprover(operator, operatorRequest);

		// Assert that the Vacation Department will review his request
		Assert.assertFalse(operatorRequest.getCompensationDepartments()
				.isEmpty());
		Assert.assertEquals(1, operatorRequest.getCompensationDepartments()
				.size());
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.VACATION));

		// Dispose session
		ksession.dispose();

	}

	@Test
	public void testExceptionalLessThanTwoMonths() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		List<String> firedRules = new ArrayList<String>();
		KieSession ksession = this
				.createKieSession("approvers.drl", firedRules);

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

		System.out.println(" ==> Days of exceptional leave: "
				+ AbsenceService.getAbsenceDays(operator,
						operatorRequest.getPlannedStartDate(),
						operatorRequest.getPlannedEndDate()));

		// Add Globals
		ksession.setGlobal("vacationDepartment",
				CompensationDepartment.VACATION);
		ksession.setGlobal("payrollDepartment", CompensationDepartment.PAYROLL);

		// Add facts to session
		KieTestHelper.insert(ksession, operatorRequest);

		// Fire rules
		System.out.println("=== FIRING RULES ===");
		ksession.fireAllRules();

		// Assert duration, less than two months
		Assert.assertTrue(AbsenceService.getAbsenceDays(operator,
				operatorRequest.getPlannedStartDate(),
				operatorRequest.getPlannedEndDate()) <= 60);

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());

		// Assert approvers: his supervisor and GM of his area
		Assert.assertEquals(2, operatorRequest.getApprovers().size());
		assertSupervisorIsApprover(operator, operatorRequest);
		Assert.assertTrue(operatorRequest.getApprovers().contains(gm));

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
		ksession.dispose();

	}

	@Test
	public void testExceptionalMoreThanTwoMonths() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		List<String> firedRules = new ArrayList<String>();
		KieSession ksession = this
				.createKieSession("approvers.drl", firedRules);

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

		System.out.println(" ==> Days of exceptional leave: "
				+ AbsenceService.getAbsenceDays(operator,
						operatorRequest.getPlannedStartDate(),
						operatorRequest.getPlannedEndDate()));

		// Add Globals
		ksession.setGlobal("vacationDepartment",
				CompensationDepartment.VACATION);
		ksession.setGlobal("payrollDepartment", CompensationDepartment.PAYROLL);

		// Add facts to session
		KieTestHelper.insert(ksession, operatorRequest);

		// Fire rules
		System.out.println("=== FIRING RULES ===");
		ksession.fireAllRules();

		// Assert duration, more than two months
		Assert.assertTrue(AbsenceService.getAbsenceDays(operator,
				operatorRequest.getPlannedStartDate(),
				operatorRequest.getPlannedEndDate()) > 60);

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());

		// Assert approvers: his supervisor, GM and VP of his area
		Assert.assertEquals(3, operatorRequest.getApprovers().size());
		assertSupervisorIsApprover(operator, operatorRequest);
		Assert.assertTrue(operatorRequest.getApprovers().contains(gm));
		Assert.assertTrue(operatorRequest.getApprovers().contains(vp));

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
		ksession.dispose();

	}

	@Test
	public void testMarriage() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		List<String> firedRules = new ArrayList<String>();
		KieSession ksession = this
				.createKieSession("approvers.drl", firedRules);

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

		// Add facts to session
		KieTestHelper.insert(ksession, operatorRequest);

		// Fire rules
		System.out.println("=== FIRING RULES ===");
		ksession.fireAllRules();

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());

		// Assert approvers: his supervisor
		Assert.assertEquals(1, operatorRequest.getApprovers().size());
		assertSupervisorIsApprover(operator, operatorRequest);

		// Assert that the Vacation Department will review his request
		Assert.assertFalse(operatorRequest.getCompensationDepartments()
				.isEmpty());
		Assert.assertEquals(1, operatorRequest.getCompensationDepartments()
				.size());
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.VACATION));

		// Dispose session
		ksession.dispose();
	}

	@Test
	public void testPaternity() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		List<String> firedRules = new ArrayList<String>();
		KieSession ksession = this
				.createKieSession("approvers.drl", firedRules);

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

		// Add facts to session
		KieTestHelper.insert(ksession, operatorRequest);

		// Fire rules
		System.out.println("=== FIRING RULES ===");
		ksession.fireAllRules();

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());

		// Assert approvers: his supervisor
		Assert.assertEquals(1, operatorRequest.getApprovers().size());
		assertSupervisorIsApprover(operator, operatorRequest);

		// Assert that the Vacation Department will review his request
		Assert.assertFalse(operatorRequest.getCompensationDepartments()
				.isEmpty());
		Assert.assertEquals(1, operatorRequest.getCompensationDepartments()
				.size());
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.VACATION));

		// Dispose session
		ksession.dispose();
	}

	@Test
	public void testTransfer() throws Exception {
		AtomicInteger id = new AtomicInteger(1);
		List<String> firedRules = new ArrayList<String>();
		KieSession ksession = this
				.createKieSession("approvers.drl", firedRules);

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

		// Add facts to session
		KieTestHelper.insert(ksession, operatorRequest);

		// Fire rules
		System.out.println("=== FIRING RULES ===");
		ksession.fireAllRules();

		// Assert that two rules were fired
		Assert.assertEquals(2, firedRules.size());

		// Assert approvers: his supervisor
		Assert.assertEquals(1, operatorRequest.getApprovers().size());
		assertSupervisorIsApprover(operator, operatorRequest);

		// Assert that the Vacation Department will review his request
		Assert.assertFalse(operatorRequest.getCompensationDepartments()
				.isEmpty());
		Assert.assertEquals(1, operatorRequest.getCompensationDepartments()
				.size());
		Assert.assertTrue(operatorRequest.getCompensationDepartments()
				.contains(CompensationDepartment.VACATION));

		// Dispose session
		ksession.dispose();
	}

	@Test
	public void testSupervisor() {
		AtomicInteger id = new AtomicInteger();

		// Create Company hierarchy
		Employee president = createPresident(id);
		Employee ceo = createCEO(id, "NameOfVP_1", president);
		Employee operator = createOperator(id, ceo);

		Assert.assertEquals(ceo, operator.getSupervisor(Role.EXECUTIVE));
		Assert.assertEquals(president, operator.getSupervisor(Role.PRESIDENT));
		Assert.assertNull(operator.getSupervisor(Role.GENERAL_MANAGER));
	}

	/*
	 * ============== HELPER METHODS ==============
	 */

	private void assertSupervisorIsApprover(Employee operator,
			LeaveRequest operatorRequest) {
		Assert.assertFalse(operatorRequest.getApprovers().isEmpty());
		Assert.assertTrue(operatorRequest.getApprovers().contains(
				operator.getDirectSupervisor()));
	}

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

	private KieSession createKieSession(String drlFile,
			final List<String> firedRules) throws Exception {
		KieSession ksession = KieTestHelper.createKieSession(drlFile);

		if (firedRules != null) {
			ksession.addEventListener(new DefaultAgendaEventListener() {
				@Override
				public void afterMatchFired(AfterMatchFiredEvent event) {
					firedRules.add(event.getMatch().getRule().getName());
				}
			});
		}
		return ksession;
	}
}
