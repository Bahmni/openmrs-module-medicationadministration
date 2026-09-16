/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhir2.apiext.providers.r4;

import static lombok.AccessLevel.PACKAGE;

import javax.annotation.Nonnull;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import lombok.Setter;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.hl7.fhir.r4.model.Patient;
import org.openmrs.module.fhir2.api.annotations.R4Provider;
import org.openmrs.module.fhir2.apiext.FhirMedicationAdministrationService;
import org.openmrs.module.fhir2.apiext.search.param.MedicationAdministrationSearchParams;
import org.openmrs.module.fhir2.providers.util.FhirProviderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("medicationAdministrationFhirR4ResourceProvider")
@R4Provider
@Setter(PACKAGE)
public class MedicationAdministrationFhirResourceProvider implements IResourceProvider {

	@Autowired
	private FhirMedicationAdministrationService fhirMedicationAdministrationService;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return MedicationAdministration.class;
	}

	@Read
	@SuppressWarnings("unused")
	public MedicationAdministration getMedicationAdministrationByUuid(@IdParam @Nonnull IdType id) {
		MedicationAdministration medicationAdministration = fhirMedicationAdministrationService.get(id.getIdPart());
		if (medicationAdministration == null) {
			throw new ResourceNotFoundException("Could not find MedicationAdministration with Id " + id.getIdPart());
		}
		return medicationAdministration;
	}

	@Create
	@SuppressWarnings("unused")
	public MethodOutcome createMedicationAdministration(@ResourceParam MedicationAdministration medicationAdministration) {
		return FhirProviderUtils.buildCreate(fhirMedicationAdministrationService.create(medicationAdministration));
	}

	@Search
	@SuppressWarnings("unused")
	public IBundleProvider searchForMedicationAdministrations(
	        @OptionalParam(name = MedicationAdministration.SP_PATIENT, chainWhitelist = { "", Patient.SP_IDENTIFIER,
	                Patient.SP_GIVEN, Patient.SP_FAMILY,
	                Patient.SP_NAME }, targetTypes = Patient.class) ReferenceAndListParam patientReference,
	        @OptionalParam(name = MedicationAdministration.SP_CONTEXT, chainWhitelist = {
	                "" }, targetTypes = Encounter.class) ReferenceAndListParam encounterReference,
	        @OptionalParam(name = MedicationAdministration.SP_STATUS) TokenAndListParam status,
	        @OptionalParam(name = MedicationAdministration.SP_EFFECTIVE_TIME) DateParam effectiveDate,
	        @OptionalParam(name = MedicationAdministration.SP_RES_ID) TokenAndListParam id) {

		return fhirMedicationAdministrationService.searchForMedicationAdministration(
		    MedicationAdministrationSearchParams.builder()
		            .patientReference(patientReference)
		            .encounterReference(encounterReference)
		            .status(status)
		            .effectiveDate(effectiveDate)
		            .id(id)
		            .build());
	}
}
