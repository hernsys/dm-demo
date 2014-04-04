package com.plugtree.dm.proj;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemHandler;

public class ProjectManagementTest {

	private KieSession ksession;
	
	@Test
	public void testRequirementProcess() {
		KieContainer kcontainer = KieServices.Factory.get().getKieClasspathContainer();
		ksession = kcontainer.newKieSession();
		WorkItemHandler htHandler = new TestAsyncWorkItemHandler();
		WorkItemHandler compileHandler = new TestAsyncWorkItemHandler();
		WorkItemHandler deployHandler = new TestAsyncWorkItemHandler();
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", htHandler);
		ksession.getWorkItemManager().registerWorkItemHandler("compile", compileHandler);
		ksession.getWorkItemManager().registerWorkItemHandler("deploy", deployHandler);

		Requirement req = new Requirement("my req", "this is a description of the requirement");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("req", req);
		ProcessInstance pi = ksession.startProcess("DMDemo.RequirementProcess", params);
		Assert.assertNotNull(pi);
	}
}
