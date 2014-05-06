package com.plugtree.dm.dmdemo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.Persistence;

import org.drools.core.util.DateUtils;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.jbpm.services.task.wih.LocalHTWorkItemHandler;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.event.KnowledgeRuntimeEventManager;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.logger.KnowledgeRuntimeLoggerFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.resource.jdbc.PoolingDataSource;

import com.plugtree.dm.core.Message;
import com.plugtree.dm.dmdemo.handlers.DisplayErrorReportHandler;
import com.plugtree.dm.dmdemo.handlers.HRDirectorHandler;
import com.plugtree.dm.dmdemo.handlers.HumanResourcesHandler;
import com.plugtree.dm.dmdemo.handlers.NotifyUserHandler;
import com.plugtree.dm.dmdemo.handlers.RollbackFormToDirectManagerHandler;
import com.plugtree.dm.dmdemo.handlers.TravelHandler;
import com.plugtree.util.KieTestHelper;

/**
 * Test Case for the Vacation Process
 * 
 * @author Ezequiel Grande
 * 
 */
public class VacationProcessTest {
	private static final String HUMAN_TASK = "Human Task";
	private static final Logger logger = LoggerFactory
			.getLogger(VacationProcessTest.class);
	private static final String VACATION_PROCESS_ID = "DMDemo.VacationProcess";
	private static final String TRAVEL = "Travel";
	private static final String HUMAN_RESOURCES = "HumanResources";
	private static final String HR_DIRECTOR = "HRDirector";
	private static final String DISPLAY_ERROR_REPORT = "Display Error Report";
	private static final String NOTIFY_USER = "NotifyUser";
	private PoolingDataSource ds;
	private static final KieContainer kcontainer = KieTestHelper
			.createKieContainer("VacationProcess.bpmn2", "HRProcess.bpmn2",
					"HRVacation.bpmn2", "vacation.drl");

	protected List<String> triggeredWorkItemNodes = new ArrayList<String>();

	@Before
	public void beforeTest() {
		System.setProperty("drools.dateformat", "yyyyMMdd");
		triggeredWorkItemNodes.clear();

		// Datasource
		ds = new PoolingDataSource();
		ds.setUniqueName("jdbc/jbpm-ds");
		ds.setClassName("org.h2.jdbcx.JdbcDataSource");
		ds.setMaxPoolSize(6);
		ds.setAllowLocalTransactions(true);
		ds.getDriverProperties().setProperty("URL",
				"jdbc:h2:tasks;MVCC=true;DB_CLOSE_ON_EXIT=TRUE");
		ds.getDriverProperties().setProperty("user", "sa");
		ds.getDriverProperties().setProperty("password", "sasa");
		ds.init();
	}

	@After
	public void tearDown() throws Exception {
		if (this.ds != null) {
			this.ds.close();
		}
	}

	/**
	 * Test scenario:<br>
	 * <ul>
	 * <li>Business Process starts without a Leave Request
	 * <li>Error will be displayed and process aborted
	 * </ul>
	 */
	@Test
	public void testLeaveRequestNotFound() {
		logger.info("======== Test Case: Leave Request Not Found ========");
		final KieSession ksession = KieTestHelper.createKieSession(kcontainer);
		// Error tracking
		List<Message> messages = new ArrayList<Message>();
		ksession.setGlobal("messages", messages);
		ksession.setGlobal("logger", logger);

		// Add Work item handlers for service and human tasks
		registerWorkItemHandlers(ksession);
		// Register Listener for testing purposes
		ksession.addEventListener(new TestProcessEventListener());
		addEventListenerRuleflowGroup(ksession);
		// Start the process, with the Operator's leave request
		Map<String, Object> params = new HashMap<String, Object>();
		WorkflowProcessInstance processInstance = (RuleFlowProcessInstance) ksession
				.createProcessInstance(VACATION_PROCESS_ID, params);
		// Assert process created
		Assert.assertNotNull(processInstance);
		Assert.assertEquals(ProcessInstance.STATE_PENDING,
				processInstance.getState());

		ksession.insert(processInstance);

		ksession.startProcessInstance(processInstance.getId());
		// Assert that the Process is aborted, because of a failure in
		// validInputs
		Assert.assertEquals(ProcessInstance.STATE_ABORTED,
				processInstance.getState());

		assertWorkItemsTriggered(DISPLAY_ERROR_REPORT);

		// Assert the expected Error
		Assert.assertEquals(1, messages.size());
		Assert.assertEquals(
				"Working Memory does not contain an instance of LeaveRequest",
				messages.get(0).getMsg());
		Assert.assertEquals(Message.Level.ERROR, messages.get(0).getLevel());

		// Dispose the knowledge session
		ksession.dispose();
	}

	/**
	 * Test scenario:<br>
	 * <ul>
	 * <li>Employee requests a Leave with invalid dates
	 * <li>Error will be displayed and process aborted
	 * </ul>
	 */
	@Test
	public void testPlannedDatesInvalid() {
		logger.info("======== Test Case: Planned Dates Invalid ========");
		final KieSession ksession = KieTestHelper.createKieSession(kcontainer);
		// Error tracking
		List<Message> messages = new ArrayList<Message>();
		ksession.setGlobal("messages", messages);
		ksession.setGlobal("logger", logger);

		// Add Work item handlers for service and human tasks
		registerWorkItemHandlers(ksession);
		// Register Listener for testing purposes
		ksession.addEventListener(new TestProcessEventListener());
		addEventListenerRuleflowGroup(ksession);

		AtomicInteger id = new AtomicInteger(1);
		// Create Company hierarchy
		Employee president = createPresident(id);
		Employee ceo = createCEO(id, "NameOfVP_1", president);
		Employee operator = createOperator(id, ceo);

		// Operator requests Annual Leave without payment
		LeaveRequest operatorRequest = createOperatorRequestBuilder(operator,
				LeaveType.ANNUAL, false).build();
		operatorRequest.setPlannedEndDate(DateUtils.parseDate("20140101"));
		operatorRequest.setPlannedStartDate(DateUtils.parseDate("20140201"));
		// Add Globals
		ksession.setGlobal("vacationDepartment",
				CompensationDepartment.VACATION);

		// Start the process, with the Operator's leave request
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("leaveRequest", operatorRequest);
		params.put("approvals", operatorRequest.getApprovals());

		WorkflowProcessInstance processInstance = (RuleFlowProcessInstance) ksession
				.createProcessInstance(VACATION_PROCESS_ID, params);
		// Assert process created
		Assert.assertNotNull(processInstance);
		Assert.assertEquals(ProcessInstance.STATE_PENDING,
				processInstance.getState());

		// Insert the Process session into the KieSession, so rules can do their
		// job
		ksession.insert(processInstance);

		ksession.startProcessInstance(processInstance.getId());

		// Assert that the Process is aborted, because of a failure in
		// validInputs
		Assert.assertEquals(ProcessInstance.STATE_ABORTED,
				processInstance.getState());

		assertWorkItemsTriggered(DISPLAY_ERROR_REPORT);

		// Assert the expected Error
		Assert.assertEquals(1, messages.size());
		Assert.assertEquals(
				"The planned end date must be after the planned start date",
				messages.get(0).getMsg());
		Assert.assertEquals(Message.Level.ERROR, messages.get(0).getLevel());

		// Dispose the knowledge session
		ksession.dispose();
	}

	/**
	 * Test scenario:<br>
	 * <ul>
	 * <li>Employee requests a Leave
	 * <li>Direct Manager will Rollback the Form to Manager
	 * <li>Subprocesses will be executed based on the Rollback status
	 * </ul>
	 */
	@Test
	public void testManagerApprovalHumanTask() {
		logger.info("======== Test Case: Manager Approval Human Task ========");

		AtomicInteger id = new AtomicInteger(1);
		// Create Company hierarchy
		Employee president = createPresident(id);
		Employee ceo = createCEO(id, "NameOfVP_1", president);
		Employee operator = createOperator(id, ceo);

		// Register the users so they can claim human tasks
		Properties userGroups = new Properties();
		userGroups.put(String.valueOf(president.getId()), "approvers");
		userGroups.put(String.valueOf(ceo.getId()), "approvers");

		RuntimeEnvironment environment = createRuntimeEnvironment(userGroups);
		// Manager and Engine Configuration
		RuntimeManager manager = RuntimeManagerFactory.Factory.get()
				.newSingletonRuntimeManager(environment);
		RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
		// Retrieve Service and Session
		TaskService taskService = engine.getTaskService();
		final KieSession ksession = engine.getKieSession();
		KnowledgeRuntimeLoggerFactory
				.newConsoleLogger((KnowledgeRuntimeEventManager) ksession);

		// Error tracking
		List<Message> messages = new ArrayList<Message>();
		ksession.setGlobal("messages", messages);
		ksession.setGlobal("logger", logger);

		// Add Work item handlers for service and human tasks
		registerWorkItemHandlers(ksession);
		LocalHTWorkItemHandler htHandler = new LocalHTWorkItemHandler();
		htHandler.setRuntimeManager(manager);
		ksession.getWorkItemManager().registerWorkItemHandler(HUMAN_TASK,
				htHandler);

		// Register Listener for testing purposes
		ksession.addEventListener(new TestProcessEventListener());
		addEventListenerRuleflowGroup(ksession);

		// Operator requests Annual Leave without payment
		LeaveRequest operatorRequest = createOperatorRequestBuilder(operator,
				LeaveType.ANNUAL, false).build();

		// Add Globals
		ksession.setGlobal("vacationDepartment",
				CompensationDepartment.VACATION);
		Employee hrvp = createHRVP(id, president);
		ksession.setGlobal("hrvp", hrvp);

		// Start the process, with the Operator's leave request
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("leaveRequest", operatorRequest);
		params.put("requestType", LeaveRequest.Type.CANCEL);
		params.put("hasTravel", Boolean.TRUE);
		params.put("requiresHRDirector", Boolean.FALSE);
		params.put("requiresHR", Boolean.FALSE);
		params.put("approvalType", ApprovalType.ROLLBACK_MANAGER);

		// Insert into the session the list of Leave request approvals
		params.put("approvals", operatorRequest.getApprovals());

		WorkflowProcessInstance processInstance = (RuleFlowProcessInstance) ksession
				.createProcessInstance(VACATION_PROCESS_ID, params);
		// Assert process created
		Assert.assertNotNull(processInstance);
		Assert.assertEquals(ProcessInstance.STATE_PENDING,
				processInstance.getState());

		// Insert the Process session into the KieSession, so rules can do their
		// job
		ksession.insert(processInstance);

		ksession.startProcessInstance(processInstance.getId());

		// Assert that the Process is still active and the Manager Approval has
		// been triggered
		Assert.assertEquals("State is not Active",
				ProcessInstance.STATE_ACTIVE, processInstance.getState());

		Assert.assertTrue("The request should not has Pending Approvals",
				operatorRequest.hasPendingApprovals());
		Assert.assertEquals(1, operatorRequest.getApprovals().size());

		// The Operator has requested a leave, and his direct supervisor will
		// work on the human task
		String approverId = String.valueOf(operator.getDirectSupervisor()
				.getId());
		List<TaskSummary> approvalTasksSum = taskService.getTasksOwned(
				approverId, "en-UK");
		Assert.assertEquals(
				"Direct Supervisor does not have one available task for him",
				1, approvalTasksSum.size());
		// Get Task
		Task approvalTask = taskService.getTaskById(approvalTasksSum.get(0)
				.getId());
		// The task should be already reserved to the Direct Supervisor
		Assert.assertEquals("Approval Task is not Reserved", Status.Reserved,
				approvalTask.getTaskData().getStatus());
		// Start the task
		taskService.start(approvalTask.getId(), approverId);
		// Refresh task data
		approvalTask = taskService.getTaskById(approvalTasksSum.get(0).getId());
		Assert.assertEquals(
				"After starting Approval Task, it's status is not In Progress",
				Status.InProgress, approvalTask.getTaskData().getStatus());

		// HUMAN TASK - Change Type to ROLLBACK_MANAGER
		Map<String, Object> approvalTaskContent = getTaskContentMap(
				environment, taskService, approvalTask);
		LeaveApproval leaveApproval = (LeaveApproval) approvalTaskContent
				.get("in_approval");
		logger.debug("====> Changing LeaveApproval to ApproalType.ROLLBACK_MANAGER");
		leaveApproval.setType(ApprovalType.ROLLBACK_MANAGER);
		Map<String, Object> managerApprovalOutput = new HashMap<String, Object>(
				1);
		managerApprovalOutput.put("out_approval", leaveApproval);
		logger.debug("====> Completing User Task...");
		taskService.complete(approvalTask.getId(), approverId,
				managerApprovalOutput);

		Assert.assertFalse("The request should not has Pending Approvals",
				operatorRequest.hasPendingApprovals());
		// Refresh task data
		approvalTask = taskService.getTaskById(approvalTasksSum.get(0).getId());
		Assert.assertEquals(
				"After starting Approval Task, it's status is not Completed",
				Status.Completed, approvalTask.getTaskData().getStatus());

		Assert.assertEquals("State is not Completed",
				ProcessInstance.STATE_COMPLETED, processInstance.getState());

		assertWorkItemsTriggered("Manager Approval",
				"Rollback Form to Direct Manager");

		logger.debug("====> Disposing RuntimeEngine....");
		manager.disposeRuntimeEngine(engine);
		manager.close();
	}

	/**
	 * Test scenario:<br>
	 * <ul>
	 * <li>Employee requests a Leave
	 * <li>Direct Manager will send the request for a Review
	 * <li>Reviewer will Reject the request
	 * <li>Direct Manager will keep the request as Rejected
	 * <li>Subprocesses will be executed based  on the Rejected status
	 * </ul>
	 */
	@Test
	public void testReviewRequiredHumanTask() {
		logger.info("======== Test Case: Review Required Human Task ========");

		AtomicInteger id = new AtomicInteger(1);
		// Create Company hierarchy
		Employee president = createPresident(id);
		Employee ceo = createCEO(id, "NameOfVP_1", president);
		Employee operator = createOperator(id, ceo);

		// Register the users so they can claim human tasks
		Properties userGroups = new Properties();
		userGroups.put(String.valueOf(president.getId()), "approvers");
		userGroups.put(String.valueOf(ceo.getId()), "approvers");

		RuntimeEnvironment environment = createRuntimeEnvironment(userGroups);
		// Manager and Engine Configuration
		RuntimeManager manager = RuntimeManagerFactory.Factory.get()
				.newSingletonRuntimeManager(environment);
		RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
		// Retrieve Service and Session
		TaskService taskService = engine.getTaskService();
		final KieSession ksession = engine.getKieSession();
		KnowledgeRuntimeLoggerFactory
				.newConsoleLogger((KnowledgeRuntimeEventManager) ksession);

		// Error tracking
		List<Message> messages = new ArrayList<Message>();
		ksession.setGlobal("messages", messages);
		ksession.setGlobal("logger", logger);

		// Add Work item handlers for service and human tasks
		registerWorkItemHandlers(ksession);
		LocalHTWorkItemHandler htHandler = new LocalHTWorkItemHandler();
		htHandler.setRuntimeManager(manager);
		ksession.getWorkItemManager().registerWorkItemHandler(HUMAN_TASK,
				htHandler);

		// Register Listener for testing purposes
		ksession.addEventListener(new TestProcessEventListener());
		addEventListenerRuleflowGroup(ksession);

		// Operator requests Annual Leave without payment
		LeaveRequest operatorRequest = createOperatorRequestBuilder(operator,
				LeaveType.ANNUAL, false).build();

		// Add Globals
		ksession.setGlobal("vacationDepartment",
				CompensationDepartment.VACATION);
		Employee hrvp = createHRVP(id, president);
		ksession.setGlobal("hrvp", hrvp);

		// Start the process, with the Operator's leave request
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("leaveRequest", operatorRequest);
		params.put("requestType", LeaveRequest.Type.CANCEL);
		params.put("hasTravel", Boolean.TRUE);
		params.put("requiresHRDirector", Boolean.FALSE);
		params.put("requiresHR", Boolean.FALSE);
		params.put("approvalType", ApprovalType.ROLLBACK_MANAGER);

		// Insert into the session the list of Leave request approvals
		params.put("approvals", operatorRequest.getApprovals());

		WorkflowProcessInstance processInstance = (RuleFlowProcessInstance) ksession
				.createProcessInstance(VACATION_PROCESS_ID, params);
		// Assert process created
		Assert.assertNotNull(processInstance);
		Assert.assertEquals(ProcessInstance.STATE_PENDING,
				processInstance.getState());

		// Insert the Process session into the KieSession, so rules can do their
		// job
		ksession.insert(processInstance);

		ksession.startProcessInstance(processInstance.getId());

		// Assert that the Process is still active and the Manager Approval has
		// been triggered
		Assert.assertEquals("State is not Active",
				ProcessInstance.STATE_ACTIVE, processInstance.getState());

		Assert.assertTrue("The request should not has Pending Approvals",
				operatorRequest.hasPendingApprovals());
		Assert.assertEquals(1, operatorRequest.getApprovals().size());

		/*
		 * HUMAN TASK: Manager Approval - Change Type to REVIEW_REQUIRED
		 */
		// The Operator has requested a leave, and his direct supervisor will
		// work on the human task
		String approverId = String.valueOf(operator.getDirectSupervisor()
				.getId());
		List<TaskSummary> approvalTasksSum = taskService.getTasksOwned(
				approverId, "en-UK");
		Assert.assertEquals(
				"Direct Supervisor does not have one available task for him",
				1, approvalTasksSum.size());
		// Get Task
		Task approvalTask = taskService.getTaskById(approvalTasksSum.get(0)
				.getId());
		// The task should be already reserved to the Direct Supervisor
		Assert.assertEquals("Approval Task is not Reserved", Status.Reserved,
				approvalTask.getTaskData().getStatus());
		// Start the task
		taskService.start(approvalTask.getId(), approverId);
		// Refresh task data
		approvalTask = taskService.getTaskById(approvalTasksSum.get(0).getId());
		Assert.assertEquals(
				"After starting Approval Task, it's status is not In Progress",
				Status.InProgress, approvalTask.getTaskData().getStatus());

		Map<String, Object> approvalTaskContent = getTaskContentMap(
				environment, taskService, approvalTask);
		LeaveApproval leaveApproval = (LeaveApproval) approvalTaskContent
				.get("in_approval");
		logger.debug("====> Manager Approval: changing LeaveApproval to ApproalType.REVIEW_REQUIRED");
		leaveApproval.setType(ApprovalType.REVIEW_REQUIRED);
		Map<String, Object> managerApprovalOutput = new HashMap<String, Object>(
				1);
		managerApprovalOutput.put("out_approval", leaveApproval);
		logger.debug("====> Completing Manager Approval User Task...");
		taskService.complete(approvalTask.getId(), approverId,
				managerApprovalOutput);

		/*
		 * HUMAN TASK: Review Required - Change Type to REJECTED
		 */
		// Get the Manager of the operator's direct supervisor
		String reviewerId = String.valueOf(operator.getDirectSupervisor()
				.getDirectSupervisor().getId());
		List<TaskSummary> reviewTasksSum = taskService.getTasksOwned(
				reviewerId, "en-UK");
		Assert.assertEquals(
				"Reviewer does not have one available task for him", 1,
				reviewTasksSum.size());
		// Get Task
		Task reviewTask = taskService
				.getTaskById(reviewTasksSum.get(0).getId());
		// The task should be already reserved to the Reviewer
		Assert.assertEquals("Review Task is not Reserved", Status.Reserved,
				reviewTask.getTaskData().getStatus());
		// Start the task
		taskService.start(reviewTask.getId(), reviewerId);
		// Refresh task data
		reviewTask = taskService.getTaskById(reviewTasksSum.get(0).getId());
		Assert.assertEquals(
				"After starting Review Task, it's status is not In Progress",
				Status.InProgress, reviewTask.getTaskData().getStatus());
		Map<String, Object> reviewTaskContent = getTaskContentMap(environment,
				taskService, reviewTask);
		leaveApproval = (LeaveApproval) reviewTaskContent.get("in_approval");
		logger.debug("====> Review required: changing LeaveApproval to ApproalType.REJECTED");
		leaveApproval.setType(ApprovalType.REJECTED);
		Map<String, Object> reviewRequiredOutput = new HashMap<String, Object>(
				1);
		reviewRequiredOutput.put("out_approval", leaveApproval);
		logger.debug("====> Completing Revier Required User Task...");
		taskService.complete(reviewTask.getId(), reviewerId,
				reviewRequiredOutput);
		// Refresh task data
		reviewTask = taskService.getTaskById(reviewTasksSum.get(0).getId());
		Assert.assertEquals(
				"After completing Review Task, it's status is not Completed",
				Status.Completed, reviewTask.getTaskData().getStatus());

		/*
		 * HUMAN TASK: Manager Approval - Change Type to ROLLBACK_MANAGER
		 */
		approvalTasksSum = taskService.getTasksOwned(approverId, "en-UK");
		// User now has two tasks owned: the completed before the review and the new one after the review
		Assert.assertEquals(
				"Direct Supervisor does not have two tasks owned by him",
				2, approvalTasksSum.size());
		// We want to work on the Task that is in Reserved status
		approvalTasksSum = taskService.getTasksOwnedByStatus(approverId, Arrays.asList(Status.Reserved), "en-UK");
		Assert.assertEquals(
				"Direct Supervisor does not have one reserved task for him",
				1, approvalTasksSum.size());
		// Get Task
		approvalTask = taskService.getTaskById(approvalTasksSum.get(0)
				.getId());
		// The task should be already reserved to the Direct Supervisor
		Assert.assertEquals("Approval Task is not Reserved", Status.Reserved,
				approvalTask.getTaskData().getStatus());
		// Start the task
		taskService.start(approvalTask.getId(), approverId);
		// Refresh task data
		approvalTask = taskService.getTaskById(approvalTasksSum.get(0).getId());
		Assert.assertEquals(
				"After starting Approval Task, it's status is not In Progress",
				Status.InProgress, approvalTask.getTaskData().getStatus());

		approvalTaskContent = getTaskContentMap(environment, taskService,
				approvalTask);
		leaveApproval = (LeaveApproval) approvalTaskContent.get("in_approval");
		logger.debug("====> Changing LeaveApproval to ApproalType.ROLLBACK_MANAGER");
		leaveApproval.setType(ApprovalType.ROLLBACK_MANAGER);
		managerApprovalOutput = new HashMap<String, Object>(1);
		managerApprovalOutput.put("out_approval", leaveApproval);
		logger.debug("====> Completing User Task...");
		taskService.complete(approvalTask.getId(), approverId,
				managerApprovalOutput);
		// Refresh task data
		reviewTask = taskService.getTaskById(approvalTasksSum.get(0).getId());
		Assert.assertEquals(
				"After completing Approval Task, it's status is not Completed",
				Status.Completed, reviewTask.getTaskData().getStatus());

		Assert.assertFalse("The request should not has Pending Approvals",
				operatorRequest.hasPendingApprovals());

		Assert.assertEquals("State is not Completed",
				ProcessInstance.STATE_COMPLETED, processInstance.getState());

		assertWorkItemsTriggered("Manager Approval", "Review required",
				"Manager Approval", "Rollback Form to Direct Manager");

		logger.debug("====> Disposing RuntimeEngine....");

		manager.disposeRuntimeEngine(engine);
		manager.close();
	}

	private RuntimeEnvironment createRuntimeEnvironment(Properties userGroups) {
		// Environment Configuration
		RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory
				.get()
				.newDefaultInMemoryBuilder()
				.entityManagerFactory(
						Persistence
								.createEntityManagerFactory("org.jbpm.persistence.jpa"))
				.userGroupCallback(new JBossUserGroupCallbackImpl(userGroups))
				.addAsset(
						ResourceFactory
								.newClassPathResource("VacationProcess.bpmn2"),
						ResourceType.BPMN2)
				.addAsset(
						ResourceFactory.newClassPathResource("HRProcess.bpmn2"),
						ResourceType.BPMN2)
				.addAsset(
						ResourceFactory
								.newClassPathResource("HRVacation.bpmn2"),
						ResourceType.BPMN2)
				.addAsset(ResourceFactory.newClassPathResource("vacation.drl"),
						ResourceType.DRL).get();
		return environment;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getTaskContentMap(
			RuntimeEnvironment environment, TaskService taskService, Task task) {
		byte[] taskContentBytes = taskService.getContentById(
				task.getTaskData().getDocumentContentId()).getContent();
		Object taskContent = ContentMarshallerHelper.unmarshall(
				taskContentBytes, environment.getEnvironment());
		return (Map<String, Object>) taskContent;
	}

	private void addEventListenerRuleflowGroup(final KieSession ksession) {
		ksession.addEventListener(new DefaultAgendaEventListener() {
			@Override
			public void afterRuleFlowGroupActivated(
					RuleFlowGroupActivatedEvent event) {
				logger.info("Rule Flow group activated! "
						+ event.getRuleFlowGroup().getName());
				// Fire all rules and continue processing
				ksession.fireAllRules();
			}

		});
	}

	/**
	 * Asserts that the work items that were triggered have the name of the
	 * strings sent as parameter, in the same order
	 * 
	 * @param workItemNames
	 */
	private void assertWorkItemsTriggered(String... workItemNames) {
		Assert.assertEquals(workItemNames.length, triggeredWorkItemNodes.size());
		for (int i = 0; i < workItemNames.length; i++) {
			Assert.assertTrue(
					"Work Item "
							+ workItemNames[i]
							+ " has not been triggered as expected!! Triggered Work Item Nodes: "
							+ triggeredWorkItemNodes,
					workItemNames[i].equals(triggeredWorkItemNodes.get(i)));
		}
	}

	private void registerWorkItemHandlers(KieSession ksession) {
		ksession.getWorkItemManager().registerWorkItemHandler(
				"RollbackFormToDirectManager",
				new RollbackFormToDirectManagerHandler());
		ksession.getWorkItemManager().registerWorkItemHandler(TRAVEL,
				new TravelHandler());
		ksession.getWorkItemManager().registerWorkItemHandler(HUMAN_RESOURCES,
				new HumanResourcesHandler());
		ksession.getWorkItemManager().registerWorkItemHandler(HR_DIRECTOR,
				new HRDirectorHandler());
		ksession.getWorkItemManager().registerWorkItemHandler(
				"DisplayErrorReport", new DisplayErrorReportHandler());
		ksession.getWorkItemManager().registerWorkItemHandler(NOTIFY_USER,
				new NotifyUserHandler());
	}

	private final class TestProcessEventListener implements
			ProcessEventListener {

		public void beforeVariableChanged(ProcessVariableChangedEvent event) {
		}

		public void beforeProcessStarted(ProcessStartedEvent event) {
		}

		public void beforeProcessCompleted(ProcessCompletedEvent event) {
		}

		public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
			logger.debug("=====> Before node triggered: "
					+ event.getNodeInstance().getNode().getClass() + " -- "
					+ event.getNodeInstance().getNodeName());
		}

		public void beforeNodeLeft(ProcessNodeLeftEvent event) {
		}

		public void afterVariableChanged(ProcessVariableChangedEvent event) {
			logger.debug("====> Variable " + event.getVariableInstanceId()
					+ " has changed its value from " + event.getOldValue()
					+ " to " + event.getNewValue());
		}

		public void afterProcessStarted(ProcessStartedEvent event) {
			logger.debug("=====> Starting process: "
					+ event.getProcessInstance().getProcessName());
		}

		public void afterProcessCompleted(ProcessCompletedEvent event) {
			logger.debug("=====> Completed process: "
					+ event.getProcessInstance().getProcessName());
		}

		public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
			if (event.getNodeInstance().getNode() instanceof WorkItemNode) {
				triggeredWorkItemNodes.add(((WorkItemNode) event
						.getNodeInstance().getNode()).getName());
			}
		}

		public void afterNodeLeft(ProcessNodeLeftEvent event) {
		}
	}

	/*
	 * ============== HELPER METHODS ==============
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

	private Employee createCEO(AtomicInteger id, String name,
			Employee directSupervisor) {
		return new Employee.Builder(id.getAndIncrement(), name)
				.basicSalary(BigDecimal.valueOf(15000)).email("jim@mail.com")

				.jobStartDate(DateUtils.parseDate("19990101"))
				.phone("555-555-555").position("CEO").role(Role.EXECUTIVE)
				.directSupervisor(directSupervisor).build();
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
