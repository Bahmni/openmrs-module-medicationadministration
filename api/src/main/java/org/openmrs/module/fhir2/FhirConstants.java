/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhir2;

public class FhirConstants {
	
	private FhirConstants() {
	}

	public static final String PATIENT_REFERENCE_SEARCH_HANDLER = "patient.reference.search.handler";

	public static final String ENCOUNTER_REFERENCE_SEARCH_HANDLER = "encounter.reference.search.handler";

	public static final String MEDICATION_REQUEST_REFERENCE_SEARCH_HANDLER = "medicationRequest.reference.search.handler";

	public static final String MEDICATION_REFERENCE_SEARCH_HANDLER = "medication.reference.search.handler";

	public static final String STATUS_SEARCH_HANDLER = "status.search.handler";

	public static final String COMMON_SEARCH_HANDLER = "common.search.handler";
	// above constants are needed to be removed when merging to fhir2 module


	public static final String MEDICATION_ADMINISTRATION_PERFORMER_SEARCH_HANDLER = "medicationAdministration.performer.search.handler";

	public static final String MEDICATION_ADMINISTRATION_SUPPORT_INFO_SEARCH_HANDLER = "medicationAdministration.supportInfo.search.handler";

}
