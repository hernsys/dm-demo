package com.plugtree.util;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.event.KnowledgeRuntimeEventManager;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.logger.KnowledgeRuntimeLoggerFactory;

public class KieTestHelper {
	
	/**
	 * Creates and returns a KieContainer from the resources sent as parameter.
	 * 
	 * @param resources
	 * @return the created KieContainer
	 */
	public static KieContainer createKieContainer(String... resources) {
		KieServices ks = KieServices.Factory.get();
		KieFileSystem kfs = ks.newKieFileSystem();
		for (String r : resources)
			kfs.write("src/main/resources/" + r,
					ResourceFactory.newClassPathResource(r));

		KieBuilder kbuilder = ks.newKieBuilder(kfs);
		kbuilder.buildAll();

		if (kbuilder.getResults().hasMessages(Message.Level.ERROR)) {
			System.err.println(kbuilder.getResults());
			throw new IllegalArgumentException("Could not parse knowledge.");
		}
		KieModule kmodule = kbuilder.getKieModule();
		return ks.newKieContainer(kmodule.getReleaseId());
	}
	
	/**
	 * Creates a KieSession from the KieContainer sent as parameter. The session will have a Console Logger.
	 * 
	 * @param resources
	 * @return the created KieSession
	 * @throws Exception
	 */
	public static KieSession createKieSession(KieContainer kcontainer) {
		KieSession ksession = kcontainer.newKieSession();
		// We can add a runtime logger to understand what is going on inside the
		// Engine
		KnowledgeRuntimeLoggerFactory
				.newConsoleLogger((KnowledgeRuntimeEventManager) ksession);

		return ksession;
	}

	/**
	 * Sysout the list of Strings as a list of Fired rules
	 * 
	 * @param firedRules
	 */
	public static void printFiredRules(List<String> firedRules) {
		System.out.println("\n ==> Fired rules: "
				+ (firedRules.size() == 0 ? " No rules fired" : ""));
		for (String rule : firedRules) {
			System.out.println(rule);
		}
	}

	/**
	 * Inserts into the KieSession the objects sent by parameter. It will return
	 * a List of FactHandles, one per each sent object (following the order of
	 * the objects/arguments)
	 * 
	 * @param kieSession
	 * @param list
	 * @return a List of FactHandles, one per each received argument (same order as the args)
	 */
	public static List<FactHandle> insert(KieSession kieSession,
			Object... objects) {
		if (objects == null || objects.length == 0) {
			return java.util.Collections.<FactHandle> emptyList();
		}
		List<FactHandle> factHandles = new ArrayList<FactHandle>(objects.length);
		for (Object o : objects) {
			factHandles.add(kieSession.insert(o));
		}

		return factHandles;
	}

}
