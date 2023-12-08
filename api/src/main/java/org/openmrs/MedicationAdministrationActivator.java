package org.openmrs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.BaseModuleActivator;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class MedicationAdministrationActivator extends BaseModuleActivator {
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	public void startup() {
		System.out.println("Starting Medication Administration Module");
	}
	
	public void shutdown() {
		System.out.println("Shutting down Medication Administration Module");
	}
	
}
