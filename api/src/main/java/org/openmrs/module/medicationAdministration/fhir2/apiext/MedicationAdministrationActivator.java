package org.openmrs.module.medicationAdministration.fhir2.apiext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.BaseModuleActivator;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class MedicationAdministrationActivator extends BaseModuleActivator {
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	public void startup() {
		log.debug("Starting Medication Administration Module");
	}
	
	public void shutdown() {
		log.debug("Shutting down Medication Administration Module");
	}
	
}
