/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhir2.apiext.search.param;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import org.junit.Test;
import org.openmrs.module.fhir2.FhirConstants;
import org.openmrs.module.fhir2.api.search.param.SearchParameterMap;

public class MedicationAdministrationSearchParamsTest {

	private Object paramOf(SearchParameterMap map, String key) {
		return map.getParameters(key).get(0).getParam();
	}

	@Test
	public void toSearchParameterMap_shouldMapEachHandlerToItsOwnParam() {
		ReferenceAndListParam patient = new ReferenceAndListParam();
		ReferenceAndListParam encounter = new ReferenceAndListParam();
		ReferenceAndListParam visit = new ReferenceAndListParam();
		ReferenceAndListParam request = new ReferenceAndListParam();
		ReferenceAndListParam medication = new ReferenceAndListParam();
		ReferenceAndListParam performer = new ReferenceAndListParam();
		TokenAndListParam status = new TokenAndListParam();

		SearchParameterMap map = MedicationAdministrationSearchParams.builder().patientReference(patient)
		        .encounterReference(encounter).visitReference(visit).medicationRequestReference(request)
		        .medicationReference(medication).performerReference(performer).status(status).build()
		        .toSearchParameterMap();

		assertSame(patient, paramOf(map, FhirConstants.PATIENT_REFERENCE_SEARCH_HANDLER));
		assertSame(encounter, paramOf(map, FhirConstants.ENCOUNTER_REFERENCE_SEARCH_HANDLER));
		assertSame(visit, paramOf(map, MedicationAdministrationSearchParams.VISIT_REFERENCE_SEARCH_HANDLER));
		assertSame(request, paramOf(map, FhirConstants.MEDICATION_REQUEST_REFERENCE_SEARCH_HANDLER));
		assertSame(medication, paramOf(map, FhirConstants.MEDICATION_REFERENCE_SEARCH_HANDLER));
		assertSame(performer, paramOf(map, FhirConstants.MEDICATION_ADMINISTRATION_PERFORMER_SEARCH_HANDLER));
		assertSame(status, paramOf(map, FhirConstants.STATUS_SEARCH_HANDLER));
	}

	@Test
	public void toSearchParameterMap_shouldOmitUnsetParams() {
		SearchParameterMap map = MedicationAdministrationSearchParams.builder().status(new TokenAndListParam()).build()
		        .toSearchParameterMap();

		assertEquals(1, map.getParameters(FhirConstants.STATUS_SEARCH_HANDLER).size());
		assertFalse(map.getParameters().stream().anyMatch(e -> e.getKey().equals(FhirConstants.ENCOUNTER_REFERENCE_SEARCH_HANDLER)));
	}
}
