package com.plugtree.util;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.io.ResourceFactory;

public class DroolsTestHelper {
	/**
	 * Creates a KieSession from the drlFile. If a list of firedRules is sent, it will add
	 * an AgendaEventListener that it will add the name of each fired rule to the list.
	 * 
	 * @param drlFile
	 * @return
	 * @throws Exception
	 */
	public static KieSession createKieSession(String drlFile) throws Exception {
		System.out.println("\n===== Creating KieSession from " + drlFile + " ====");
		KieServices ks = KieServices.Factory.get();
		KieFileSystem kfs = ks.newKieFileSystem();
		kfs.write("src/main/resources/rules/" + drlFile,
				ResourceFactory.newClassPathResource(drlFile));

		KieBuilder kbuilder = ks.newKieBuilder(kfs);
		kbuilder.buildAll();

		if (kbuilder.getResults().hasMessages(Message.Level.ERROR)) {
			System.err.println(kbuilder.getResults());
			throw new IllegalArgumentException("Could not parse knowledge.");
		}
		KieModule kmodule = kbuilder.getKieModule();
		KieContainer kcontainer = ks.newKieContainer(kmodule.getReleaseId());

		KieSession ksession = kcontainer.newKieSession();
		ks.getLoggers().newConsoleLogger((KieRuntimeEventManager) ksession);

		return ksession;
	}
	
	public static void printFiredRules(List<String> firedRules) {
		System.out.println("\n ==> Fired rules: " + ( firedRules.size()==0?" No rules fired":""));
		for (String rule:firedRules) {
			System.out.println(rule);
		}
	}
	
	/**
	 * Inserts into the KieSession the objects sent by parameter. It will
	 * return a list of FactHandles, one per each sent object (following the order of the objects)
	 * 
	 * @param kieSession
	 * @param list
	 * @return
	 */
	public static List<FactHandle> insert(KieSession kieSession, Object... objects) {
		if (objects == null || objects.length ==0) {
			return java.util.Collections.<FactHandle>emptyList();
		}
		List<FactHandle> factHandles = new ArrayList<FactHandle>(objects.length);
		for (Object o:objects) {
			factHandles.add(kieSession.insert(o));
		}
		
		return factHandles;
	}

}
