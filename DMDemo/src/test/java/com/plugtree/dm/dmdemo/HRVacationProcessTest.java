package com.plugtree.dm.dmdemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.io.impl.ClassPathResource;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.internal.event.KnowledgeRuntimeEventManager;
import org.kie.internal.logger.KnowledgeRuntimeLoggerFactory;

import com.plugtree.dm.dmdemo.handlers.AcceptedNotifyManagementHandler;
import com.plugtree.dm.dmdemo.handlers.AddVacationHandler;
import com.plugtree.dm.dmdemo.handlers.CancelVacationHandler;
import com.plugtree.dm.dmdemo.handlers.EndProcessNotifyManagementHandler;
import com.plugtree.dm.dmdemo.handlers.ModifyVacationHandler;
import com.plugtree.dm.dmdemo.handlers.RollbackFormToDirectManagerHandler;
import com.plugtree.dm.dmdemo.handlers.RollbackFormToRequestorHandler;
import com.plugtree.dm.dmdemo.handlers.TravelHandler;
import com.plugtree.dm.proj.TestAsyncWorkItemHandler;

/**
 * Test Case for the HR Vacation Process
 * 
 * @author Ezequiel Grande
 * 
 */
public class HRVacationProcessTest {
	private static final String HUMAN_TASK = "Human Task";
	private static final String TRAVEL = "Travel";
	private static final String ADD_VACATION = "AddVacation";
	private static final String CANCEL_VACATION = "CancelVacation";
	private static final String MODIFY_VACATION = "ModifyVacation";
	private static final String ACCEPTED_NOTIFY_MANAGEMENT = "AcceptedNotifyManagement";
	private static final String END_PROCESS_NOTIFY_MANAGEMENT = "EndProcessNotifyManagement";
	private static final String ROLLBACK_FORM_TO_DIRECT_MANAGER = "RollbackFormToDirectManager";
	private static final String ROLLBACK_FORM_TO_REQUESTOR = "RollbackFormToRequestor";
	protected List<String> triggeredWorkItemNodes = new ArrayList<String>();

	@Before
	public void beforeTest() {
		triggeredWorkItemNodes.clear();
	}

	@Test
	public void testRollbackToDirectManager() {
		KieSession ksession = createSession();

		// Add Work item handlers for service and human tasks
		registerWorkItemHandlers(ksession);
		// Register Listener for testing purposes
		ksession.addEventListener(new TestProcessEventListener());

		// Start the process, with approval type ROLLBACK MANAGER
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("approvalType", ApprovalType.ROLLBACK_MANAGER);
		ProcessInstance processInstance = ksession.startProcess(
				"DMDemo.HRVacation", params);
		// Assert process created
		Assert.assertNotNull(processInstance);

		// Assert that the task "Rollback To Direct Manager" has been fired
		Assert.assertEquals(1, triggeredWorkItemNodes.size());
		Assert.assertTrue("Work Item " + ROLLBACK_FORM_TO_DIRECT_MANAGER
				+ " has not been triggered!!", triggeredWorkItemNodes
				.contains(ROLLBACK_FORM_TO_DIRECT_MANAGER));

		// Assert that the Process is completed
		Assert.assertEquals(ProcessInstance.STATE_COMPLETED,
				processInstance.getState());

		// Dispose the knowledge session
		ksession.dispose();

	}

	@Test
	public void testRollbackToRequestor() {
		KieSession ksession = createSession();

		// Add Work item handlers for service and human tasks
		registerWorkItemHandlers(ksession);
		// Register Listener for testing purposes
		ksession.addEventListener(new TestProcessEventListener());

		// Start the process, with approval type ROLLBACK REQUESTOR
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("approvalType", ApprovalType.ROLLBACK_REQUESTOR);
		ProcessInstance processInstance = ksession.startProcess(
				"DMDemo.HRVacation", params);
		Assert.assertNotNull(processInstance);

		// Assert that the task "Rollback To Requestor" has been fired
		Assert.assertEquals(1, triggeredWorkItemNodes.size());
		Assert.assertTrue(triggeredWorkItemNodes
				.contains(ROLLBACK_FORM_TO_REQUESTOR));

		// Assert that the Process is completed
		Assert.assertEquals(ProcessInstance.STATE_COMPLETED,
				processInstance.getState());

		// Dispose the knowledge session
		ksession.dispose();

	}

	@Test
	public void testRejected() {
		KieSession ksession = createSession();

		// Add Work item handlers for service and human tasks
		registerWorkItemHandlers(ksession);
		// Register Listener for testing purposes
		ksession.addEventListener(new TestProcessEventListener());

		// Start the process, with approval type REJECTED
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("approvalType", ApprovalType.REJECTED);
		ProcessInstance processInstance = ksession.startProcess(
				"DMDemo.HRVacation", params);
		Assert.assertNotNull(processInstance);

		// Assert that the task "End Process & Notify Management" has been fired
		Assert.assertEquals(1, triggeredWorkItemNodes.size());
		Assert.assertTrue(triggeredWorkItemNodes
				.contains(END_PROCESS_NOTIFY_MANAGEMENT));

		// Assert that the Process is completed
		Assert.assertEquals(ProcessInstance.STATE_COMPLETED,
				processInstance.getState());

		// Dispose the knowledge session
		ksession.dispose();

	}

	@Test
	public void testApproved_NotExecutive_AddVacation_Payroll_Travel() {
		KieSession ksession = createSession();

		// Add Work item handlers for service and human tasks
		registerWorkItemHandlers(ksession);
		// Payroll task -- TODO: Name should be 'Payroll', no 'Human Task'
		TestAsyncWorkItemHandler payrollHandler = new TestAsyncWorkItemHandler();
		ksession.getWorkItemManager().registerWorkItemHandler(HUMAN_TASK,
				payrollHandler);

		// Register Listener for testing purposes
		ksession.addEventListener(new TestProcessEventListener());

		// Start the process, with approval type APPROVED
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("approvalType", ApprovalType.APPROVED);
		params.put("isExecutive", Boolean.FALSE);
		params.put("requestType", LeaveRequest.Type.NEW);
		params.put("hasPayrollEffect", Boolean.TRUE);
		params.put("hasTravel", Boolean.TRUE);

		ProcessInstance processInstance = ksession.startProcess(
				"DMDemo.HRVacation", params);
		Assert.assertNotNull(processInstance);

		// Assert that the Process is still active, awaiting for Payroll Human
		// Task
		Assert.assertEquals(ProcessInstance.STATE_ACTIVE,
				processInstance.getState());

		WorkItem payrollItem = payrollHandler.getWorkItem();
		Assert.assertNotNull(payrollItem);
		// TODO: Inputs / Outputs

		// Complete the Payroll Human Task
		ksession.getWorkItemManager().completeWorkItem(payrollItem.getId(),
				null);

		// Assert that the tasks "Add Vacation", "Payroll", "Travel" have been
		// fired
		Assert.assertEquals(3, triggeredWorkItemNodes.size());
		Assert.assertTrue("Add Vacation has not been triggered!",
				triggeredWorkItemNodes.contains(ADD_VACATION));
		Assert.assertTrue("Payroll has not been triggered!",
				triggeredWorkItemNodes.contains(HUMAN_TASK));
		Assert.assertTrue("Travel has not been triggered!",
				triggeredWorkItemNodes.contains(TRAVEL));

		// Assert that the Process is completed
		Assert.assertEquals(ProcessInstance.STATE_COMPLETED,
				processInstance.getState());

		// Dispose the knowledge session
		ksession.dispose();
	}

	@Test
	public void testApproved_NotExecutive_AddVacation_NoPayroll_Travel() {
		KieSession ksession = createSession();

		// Add Work item handlers for service and human tasks
		registerWorkItemHandlers(ksession);
		// Register Listener for testing purposes
		ksession.addEventListener(new TestProcessEventListener());

		// Start the process, with approval type APPROVED
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("approvalType", ApprovalType.APPROVED);
		params.put("isExecutive", Boolean.FALSE);
		params.put("requestType", LeaveRequest.Type.NEW);
		params.put("hasPayrollEffect", Boolean.FALSE);
		params.put("hasTravel", Boolean.TRUE);

		ProcessInstance processInstance = ksession.startProcess(
				"DMDemo.HRVacation", params);
		Assert.assertNotNull(processInstance);

		// Assert that the tasks "Add Vacation", "Travel" have been fired
		Assert.assertEquals(2, triggeredWorkItemNodes.size());
		Assert.assertTrue(triggeredWorkItemNodes.contains(ADD_VACATION));
		Assert.assertTrue(triggeredWorkItemNodes.contains(TRAVEL));

		// Assert that the Process is completed
		Assert.assertEquals(ProcessInstance.STATE_COMPLETED,
				processInstance.getState());

		// Dispose the knowledge session
		ksession.dispose();
	}

	@Test
	public void testApproved_NotExecutive_AddVacation_NoPayroll_NotTravel() {
		KieSession ksession = createSession();

		// Add Work item handlers for service and human tasks
		registerWorkItemHandlers(ksession);
		// Register Listener for testing purposes
		ksession.addEventListener(new TestProcessEventListener());

		// Start the process, with approval type APPROVED
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("approvalType", ApprovalType.APPROVED);
		params.put("isExecutive", Boolean.FALSE);
		params.put("requestType", LeaveRequest.Type.NEW);
		params.put("hasPayrollEffect", Boolean.FALSE);
		params.put("hasTravel", Boolean.FALSE);

		ProcessInstance processInstance = ksession.startProcess(
				"DMDemo.HRVacation", params);
		Assert.assertNotNull(processInstance);

		// Assert that the tasks "Add Vacation", "Accepted & Notify Management"
		// have been fired
		Assert.assertEquals(2, triggeredWorkItemNodes.size());
		Assert.assertTrue(triggeredWorkItemNodes.contains(ADD_VACATION));
		Assert.assertTrue(triggeredWorkItemNodes
				.contains(ACCEPTED_NOTIFY_MANAGEMENT));

		// Assert that the Process is completed
		Assert.assertEquals(ProcessInstance.STATE_COMPLETED,
				processInstance.getState());

		// Dispose the knowledge session
		ksession.dispose();
	}


	@Test
	public void testApproved_NotExecutive_CancelVacation_NoPayroll_Travel() {
		KieSession ksession = createSession();

		// Add Work item handlers for service and human tasks
		registerWorkItemHandlers(ksession);
		// Register Listener for testing purposes
		ksession.addEventListener(new TestProcessEventListener());

		// Start the process, with approval type APPROVED
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("approvalType", ApprovalType.APPROVED);
		params.put("isExecutive", Boolean.FALSE);
		params.put("requestType", LeaveRequest.Type.CANCEL);
		params.put("hasPayrollEffect", Boolean.FALSE);
		params.put("hasTravel", Boolean.TRUE);

		ProcessInstance processInstance = ksession.startProcess(
				"DMDemo.HRVacation", params);
		Assert.assertNotNull(processInstance);

		// Assert that the tasks "Cancel Vacation", "Travel" have been fired
		Assert.assertEquals(2, triggeredWorkItemNodes.size());
		Assert.assertTrue(triggeredWorkItemNodes.contains(CANCEL_VACATION));
		Assert.assertTrue(triggeredWorkItemNodes.contains(TRAVEL));

		// Assert that the Process is completed
		Assert.assertEquals(ProcessInstance.STATE_COMPLETED,
				processInstance.getState());

		// Dispose the knowledge session
		ksession.dispose();
	}

	@Test
	public void testApproved_NotExecutive_CancelVacation_NoPayroll_NoTravel() {
		KieSession ksession = createSession();

		// Add Work item handlers for service and human tasks
		registerWorkItemHandlers(ksession);
		// Register Listener for testing purposes
		ksession.addEventListener(new TestProcessEventListener());

		// Start the process, with approval type APPROVED
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("approvalType", ApprovalType.APPROVED);
		params.put("isExecutive", Boolean.FALSE);
		params.put("requestType", LeaveRequest.Type.CANCEL);
		params.put("hasPayrollEffect", Boolean.FALSE);
		params.put("hasTravel", Boolean.FALSE);

		ProcessInstance processInstance = ksession.startProcess(
				"DMDemo.HRVacation", params);
		Assert.assertNotNull(processInstance);

		// Assert that the tasks "Cancel Vacation",
		// "Accepted & Notify Management" have been fired
		Assert.assertEquals(2, triggeredWorkItemNodes.size());
		Assert.assertTrue(triggeredWorkItemNodes.contains(CANCEL_VACATION));
		Assert.assertTrue(triggeredWorkItemNodes
				.contains(ACCEPTED_NOTIFY_MANAGEMENT));

		// Assert that the Process is completed
		Assert.assertEquals(ProcessInstance.STATE_COMPLETED,
				processInstance.getState());

		// Dispose the knowledge session
		ksession.dispose();
	}


	@Test
	public void testApproved_NotExecutive_ModifyVacation_NoPayroll_Travel() {
		KieSession ksession = createSession();

		// Add Work item handlers for service and human tasks
		registerWorkItemHandlers(ksession);
		// Register Listener for testing purposes
		ksession.addEventListener(new TestProcessEventListener());

		// Start the process, with approval type APPROVED
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("approvalType", ApprovalType.APPROVED);
		params.put("isExecutive", Boolean.FALSE);
		params.put("requestType", LeaveRequest.Type.MODIFY);
		params.put("hasPayrollEffect", Boolean.FALSE);
		params.put("hasTravel", Boolean.TRUE);

		ProcessInstance processInstance = ksession.startProcess(
				"DMDemo.HRVacation", params);
		Assert.assertNotNull(processInstance);

		// Assert that the tasks "Modify Vacation", "Travel" have been fired
		Assert.assertEquals(2, triggeredWorkItemNodes.size());
		Assert.assertTrue(triggeredWorkItemNodes.contains(MODIFY_VACATION));
		Assert.assertTrue(triggeredWorkItemNodes.contains(TRAVEL));

		// Assert that the Process is completed
		Assert.assertEquals(ProcessInstance.STATE_COMPLETED,
				processInstance.getState());

		// Dispose the knowledge session
		ksession.dispose();
	}

	@Test
	public void testApproved_NotExecutive_ModifyVacation_NoPayroll_NoTravel() {
		KieSession ksession = createSession();

		// Add Work item handlers for service and human tasks
		registerWorkItemHandlers(ksession);
		// Register Listener for testing purposes
		ksession.addEventListener(new TestProcessEventListener());

		// Start the process, with approval type APPROVED
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("approvalType", ApprovalType.APPROVED);
		params.put("isExecutive", Boolean.FALSE);
		params.put("requestType", LeaveRequest.Type.MODIFY);
		params.put("hasPayrollEffect", Boolean.FALSE);
		params.put("hasTravel", Boolean.FALSE);

		ProcessInstance processInstance = ksession.startProcess(
				"DMDemo.HRVacation", params);
		Assert.assertNotNull(processInstance);

		// Assert that the tasks "Modify Vacation",
		// "Accepted & Notify Management" have been fired
		Assert.assertEquals(2, triggeredWorkItemNodes.size());
		Assert.assertTrue(triggeredWorkItemNodes.contains(MODIFY_VACATION));
		Assert.assertTrue(triggeredWorkItemNodes
				.contains(ACCEPTED_NOTIFY_MANAGEMENT));

		// Assert that the Process is completed
		Assert.assertEquals(ProcessInstance.STATE_COMPLETED,
				processInstance.getState());

		// Dispose the knowledge session
		ksession.dispose();
	}
	
	@Test
	public void testApproved_Executive_NoPayroll_NoTravel() {
		KieSession ksession = createSession();

		// Add Work item handlers for service and human tasks
		registerWorkItemHandlers(ksession);
		// Register Listener for testing purposes
		ksession.addEventListener(new TestProcessEventListener());

		// Start the process, with approval type APPROVED
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("approvalType", ApprovalType.APPROVED);
		params.put("isExecutive", Boolean.TRUE);
		params.put("hasPayrollEffect", Boolean.FALSE);
		params.put("hasTravel", Boolean.FALSE);

		ProcessInstance processInstance = ksession.startProcess(
				"DMDemo.HRVacation", params);
		Assert.assertNotNull(processInstance);

		// Assert that the task "Accepted & Notify Management" has been fired
		Assert.assertEquals(1, triggeredWorkItemNodes.size());
		Assert.assertTrue(triggeredWorkItemNodes
				.contains(ACCEPTED_NOTIFY_MANAGEMENT));

		// Assert that the Process is completed
		Assert.assertEquals(ProcessInstance.STATE_COMPLETED,
				processInstance.getState());

		// Dispose the knowledge session
		ksession.dispose();
	}


	@Test
	public void testApproved_Executive_NoPayroll_Travel() {
		KieSession ksession = createSession();

		// Add Work item handlers for service and human tasks
		registerWorkItemHandlers(ksession);
		// Register Listener for testing purposes
		ksession.addEventListener(new TestProcessEventListener());

		// Start the process, with approval type APPROVED
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("approvalType", ApprovalType.APPROVED);
		params.put("isExecutive", Boolean.TRUE);
		params.put("hasPayrollEffect", Boolean.FALSE);
		params.put("hasTravel", Boolean.TRUE);

		ProcessInstance processInstance = ksession.startProcess(
				"DMDemo.HRVacation", params);
		Assert.assertNotNull(processInstance);

		// Assert that the task "Travel" has been fired
		Assert.assertEquals(1, triggeredWorkItemNodes.size());
		Assert.assertTrue(triggeredWorkItemNodes
				.contains(TRAVEL));

		// Assert that the Process is completed
		Assert.assertEquals(ProcessInstance.STATE_COMPLETED,
				processInstance.getState());

		// Dispose the knowledge session
		ksession.dispose();
	}

	@Test
	public void testApproved_Executive_Payroll_Travel() {
		KieSession ksession = createSession();

		// Add Work item handlers for service and human tasks
		registerWorkItemHandlers(ksession);
		// Payroll task -- TODO: Name should be 'Payroll', no 'Human Task'
		TestAsyncWorkItemHandler payrollHandler = new TestAsyncWorkItemHandler();
		ksession.getWorkItemManager().registerWorkItemHandler(HUMAN_TASK,
				payrollHandler);

		// Register Listener for testing purposes
		ksession.addEventListener(new TestProcessEventListener());

		// Start the process, with approval type APPROVED
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("approvalType", ApprovalType.APPROVED);
		params.put("isExecutive", Boolean.TRUE);
		params.put("hasPayrollEffect", Boolean.TRUE);
		params.put("hasTravel", Boolean.TRUE);

		ProcessInstance processInstance = ksession.startProcess(
				"DMDemo.HRVacation", params);
		Assert.assertNotNull(processInstance);

		// Assert that the Process is still active, awaiting for Payroll Human
		// Task
		Assert.assertEquals(ProcessInstance.STATE_ACTIVE,
				processInstance.getState());

		WorkItem payrollItem = payrollHandler.getWorkItem();
		Assert.assertNotNull(payrollItem);
		// TODO: Inputs / Outputs

		// Complete the Payroll Human Task
		ksession.getWorkItemManager().completeWorkItem(payrollItem.getId(),
				null);

		// Assert that the tasks "Payroll" and "Travel" have been fired
		Assert.assertEquals(2, triggeredWorkItemNodes.size());
		Assert.assertTrue(triggeredWorkItemNodes
				.contains(HUMAN_TASK));
		Assert.assertTrue(triggeredWorkItemNodes
				.contains(TRAVEL));

		// Assert that the Process is completed
		Assert.assertEquals(ProcessInstance.STATE_COMPLETED,
				processInstance.getState());

		// Dispose the knowledge session
		ksession.dispose();
	}

	
	
	private void registerWorkItemHandlers(KieSession ksession) {
		ksession.getWorkItemManager().registerWorkItemHandler(
				ROLLBACK_FORM_TO_DIRECT_MANAGER,
				new RollbackFormToDirectManagerHandler());
		ksession.getWorkItemManager().registerWorkItemHandler(
				ROLLBACK_FORM_TO_REQUESTOR,
				new RollbackFormToRequestorHandler());
		ksession.getWorkItemManager().registerWorkItemHandler(
				END_PROCESS_NOTIFY_MANAGEMENT,
				new EndProcessNotifyManagementHandler());
		ksession.getWorkItemManager().registerWorkItemHandler(
				ACCEPTED_NOTIFY_MANAGEMENT,
				new AcceptedNotifyManagementHandler());
		ksession.getWorkItemManager().registerWorkItemHandler(ADD_VACATION,
				new AddVacationHandler());
		ksession.getWorkItemManager().registerWorkItemHandler(MODIFY_VACATION,
				new ModifyVacationHandler());
		ksession.getWorkItemManager().registerWorkItemHandler(CANCEL_VACATION,
				new CancelVacationHandler());
		ksession.getWorkItemManager().registerWorkItemHandler(TRAVEL,
				new TravelHandler());
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
		}

		public void beforeNodeLeft(ProcessNodeLeftEvent event) {
		}

		public void afterVariableChanged(ProcessVariableChangedEvent event) {
		}

		public void afterProcessStarted(ProcessStartedEvent event) {
		}

		public void afterProcessCompleted(ProcessCompletedEvent event) {
		}

		public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
			// System.out.println(" == > TRIGGERED node "
			// + event.getNodeInstance().getNode().getClass() + " ---- "
			// + event.getNodeInstance().getNodeName());
			if (event.getNodeInstance().getNode() instanceof WorkItemNode) {
				triggeredWorkItemNodes.add(((WorkItemNode) event
						.getNodeInstance().getNode()).getWork().getName());
			}
		}

		public void afterNodeLeft(ProcessNodeLeftEvent event) {
		}
	}

	private KieSession createSession() {
		// Create a file system to add knowledge to
		KieServices ks = KieServices.Factory.get();
		KieRepository kr = ks.getRepository();
		KieFileSystem kfs = ks.newKieFileSystem();
		// Add our knowledge
		kfs.write(new ClassPathResource("HRVacation.bpmn2"));
		// Create the Knowledge Builder
		KieBuilder kbuilder = ks.newKieBuilder(kfs);
		kbuilder.buildAll();
		// Check for errors during the compilation of the rules
		Results results = kbuilder.getResults();
		List<Message> errors = results.getMessages(Message.Level.ERROR);
		if (errors.size() > 0) {
			for (Message error : errors) {
				System.err.println(error);
			}
			throw new IllegalArgumentException("Could not parse knowledge.");
		}
		// Create the Knowledge Base
		KieContainer kcont = ks.newKieContainer(kr.getDefaultReleaseId());
		KieBase kbase = kcont.newKieBase(null);
		// Create the StatefulSession using the Knowledge Base that contains
		// the compiled rules
		KieSession ksession = kbase.newKieSession();

		// We can add a runtime logger to understand what is going on inside the
		// Engine
		KnowledgeRuntimeLoggerFactory
				.newConsoleLogger((KnowledgeRuntimeEventManager) ksession);

		return ksession;
	}

}
