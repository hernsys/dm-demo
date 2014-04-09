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
import org.kie.internal.event.KnowledgeRuntimeEventManager;
import org.kie.internal.logger.KnowledgeRuntimeLoggerFactory;

import com.plugtree.dm.dmdemo.handlers.HRDirectorHandler;
import com.plugtree.dm.dmdemo.handlers.HumanResourcesHandler;
import com.plugtree.dm.dmdemo.handlers.RollbackFormToDirectManagerHandler;
import com.plugtree.dm.dmdemo.handlers.TravelHandler;

/**
 * Test Case for the HR Process
 * 
 * @author Ezequiel Grande
 * 
 */
public class HRProcessTest {
	private static final String ROLLBACK_FORM_TO_DIRECT_MANAGER = "RollbackFormToDirectManager";
	private static final String HR_PROCESS_ID = "DMDemo.HRProcess";
	private static final String TRAVEL = "Travel";
	private static final String HUMAN_RESOURCES = "HumanResources";
	private static final String HR_DIRECTOR = "HRDirector";
	protected List<String> triggeredWorkItemNodes = new ArrayList<String>();

	@Before
	public void beforeTest() {
		triggeredWorkItemNodes.clear();
	}

	@Test
	public void testCancel_Travel_NoHRDirector() {
		KieSession ksession = createSession();

		// Add Work item handlers for service and human tasks
		registerWorkItemHandlers(ksession);
		// Register Listener for testing purposes
		ksession.addEventListener(new TestProcessEventListener());

		// Start the process, with approval type ROLLBACK MANAGER
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("approvalType", ApprovalType.ROLLBACK_MANAGER);
		params.put("requestType", LeaveRequest.Type.CANCEL);
		params.put("hasTravel", Boolean.TRUE);
		params.put("requiresHRDirector", Boolean.FALSE);
		ProcessInstance processInstance = ksession.startProcess(HR_PROCESS_ID,
				params);
		// Assert process created
		Assert.assertNotNull(processInstance);

		// Assert that the task "Travel" has been fired + task in subprocess: rollback form to direct manager
		assertWorkItemsTriggered(ROLLBACK_FORM_TO_DIRECT_MANAGER, TRAVEL);

		// Assert that the Process is completed
		Assert.assertEquals(ProcessInstance.STATE_COMPLETED,
				processInstance.getState());

		// Dispose the knowledge session
		ksession.dispose();

	}

	/**
	 * Asserts that the work items that were triggered have the name of the strings sent as parameter
	 * 
	 * @param workItemNames
	 */
	private void assertWorkItemsTriggered(String... workItemNames) {
		Assert.assertEquals(workItemNames.length, triggeredWorkItemNodes.size());
		for (String name : workItemNames) {
			Assert.assertTrue(
					"Work Item " + name + " has not been triggered!!",
					triggeredWorkItemNodes.contains(name));
		}
	}

	private void registerWorkItemHandlers(KieSession ksession) {
		ksession.getWorkItemManager().registerWorkItemHandler(
				ROLLBACK_FORM_TO_DIRECT_MANAGER,
				new RollbackFormToDirectManagerHandler());
		ksession.getWorkItemManager().registerWorkItemHandler(TRAVEL,
				new TravelHandler());
		ksession.getWorkItemManager().registerWorkItemHandler(HUMAN_RESOURCES,
				new HumanResourcesHandler());
		ksession.getWorkItemManager().registerWorkItemHandler(HR_DIRECTOR,
				new HRDirectorHandler());
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
		kfs.write(new ClassPathResource("HRProcess.bpmn2"));
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
