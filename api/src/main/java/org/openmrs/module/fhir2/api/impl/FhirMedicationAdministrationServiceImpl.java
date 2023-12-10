/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhir2.api.impl;

import ca.uhn.fhir.rest.api.server.IBundleProvider;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.openmrs.module.fhir2.api.FhirMedicationAdministrationService;
import org.openmrs.module.fhir2.api.dao.FhirMedicationAdministrationDao;
import org.openmrs.module.fhir2.api.search.SearchQuery;
import org.openmrs.module.fhir2.api.search.SearchQueryInclude;
import org.openmrs.module.fhir2.api.search.param.MedicationAdministrationSearchParams;
import org.openmrs.module.fhir2.api.translators.MedicationAdministrationTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Setter(AccessLevel.PACKAGE)
@Getter(AccessLevel.PROTECTED)
public class FhirMedicationAdministrationServiceImpl extends BaseFhirService<MedicationAdministration,org.openmrs.MedicationAdministration> implements FhirMedicationAdministrationService {
	
	@Autowired
	private MedicationAdministrationTranslator translator;
	
	@Autowired
	private FhirMedicationAdministrationDao dao;
	
	@Autowired
	private SearchQueryInclude<MedicationAdministration> searchQueryInclude;
	
	@Autowired
	private SearchQuery<org.openmrs.MedicationAdministration,MedicationAdministration,FhirMedicationAdministrationDao<org.openmrs.MedicationAdministration>, MedicationAdministrationTranslator<org.openmrs.MedicationAdministration>, SearchQueryInclude<MedicationAdministration>> searchQuery;
	
	@Override
	public IBundleProvider searchForMedicationAdministration(
	        MedicationAdministrationSearchParams medicationAdministrationSearchParams) {
		return searchQuery.getQueryResults(medicationAdministrationSearchParams.toSearchParameterMap(), dao, translator,
		    searchQueryInclude);
	}
	
}
