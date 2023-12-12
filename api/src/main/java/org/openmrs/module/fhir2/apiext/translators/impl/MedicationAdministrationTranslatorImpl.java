/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhir2.apiext.translators.impl;

import static org.apache.commons.lang3.Validate.notNull;
import static org.openmrs.module.fhir2.api.translators.impl.FhirTranslatorUtils.getLastUpdated;

import javax.annotation.Nonnull;


import lombok.AccessLevel;
import lombok.Setter;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Provider;
import org.openmrs.module.fhir2.api.translators.*;
import org.openmrs.module.fhir2.apiext.translators.MedicationAdministrationStatusTranslator;
import org.openmrs.module.fhir2.apiext.translators.MedicationAdministrationTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//
@Component
@Setter(AccessLevel.PACKAGE)
public class MedicationAdministrationTranslatorImpl implements MedicationAdministrationTranslator<org.openmrs.module.fhir2.apiext.model.MedicationAdministration> {

	@Autowired
	private MedicationAdministrationStatusTranslator statusTranslator;

	@Autowired
	private PatientReferenceTranslator patientReferenceTranslator;

	@Autowired
	private EncounterReferenceTranslator<Encounter> encounterReferenceTranslator;

	@Autowired
	private PractitionerReferenceTranslator<Provider> practitionerReferenceTranslator;

	@Autowired
	private MedicationRequestReferenceTranslator medicationRequestReferenceTranslator;

	@Autowired
	private MedicationRequestTranslator medicationRequestTranslator;


	@Override
	public MedicationAdministration toFhirResource(@Nonnull org.openmrs.module.fhir2.apiext.model.MedicationAdministration openmrsMedicationAdministration) {
		notNull(openmrsMedicationAdministration, "The FhirMedicationAdministration object should not be null");

		MedicationAdministration medicationAdministration = new MedicationAdministration();
		medicationAdministration.setId(openmrsMedicationAdministration.getUuid());
		medicationAdministration.setEffective(new DateTimeType(openmrsMedicationAdministration.getAdministeredDateTime()));
		medicationAdministration.setStatus(statusTranslator.toFhirResource(openmrsMedicationAdministration.getStatus()));
		medicationAdministration
		        .setSubject(patientReferenceTranslator.toFhirResource(openmrsMedicationAdministration.getPatient()));
		medicationAdministration
		        .setContext(encounterReferenceTranslator.toFhirResource(openmrsMedicationAdministration.getEncounter()));

		if (openmrsMedicationAdministration.getAdminister() != null) {
			medicationAdministration.addPerformer().setActor(practitionerReferenceTranslator.toFhirResource(openmrsMedicationAdministration.getAdminister()));
		}




		if (openmrsMedicationAdministration.getDrugOrder() !=null)
		{
			medicationAdministration.setRequest(medicationRequestReferenceTranslator.toFhirResource(openmrsMedicationAdministration.getDrugOrder()));
		}
		else {
			// This logic is added to translate the drug info if drug order id not mapped
			//drugOrder.setConcept(openmrsMedicationAdministration.getConcept());
			DrugOrder drugOrder = new DrugOrder();
			drugOrder.setDrug(openmrsMedicationAdministration.getDrug());
			drugOrder.setDose(openmrsMedicationAdministration.getDose());
			drugOrder.setDoseUnits(openmrsMedicationAdministration.getDoseUnits());
			drugOrder.setRoute(openmrsMedicationAdministration.getRoute());
			//drugOrder.setFrequency(openmrsMedicationAdministration.getFrequency());
			//drugOrder.setAsNeeded(openmrsMedicationAdministration.getAsNeeded());
			drugOrder.setDosingInstructions(openmrsMedicationAdministration.getDosingInstructions());
			//drugOrder.setQuantity(openmrsMedicationAdministration.get);
			//drugOrder.setQuantityUnits(openmrsMedicationAdministration.getDoseUnits());
			MedicationRequest doseAndQuantity =medicationRequestTranslator.toFhirResource(openmrsMedicationAdministration.getDrugOrder());
		}

		if (openmrsMedicationAdministration.getNotes() != null) {
			medicationAdministration.addNote().setText(openmrsMedicationAdministration.getNotes());
		}

		medicationAdministration.getMeta().setLastUpdated(getLastUpdated(openmrsMedicationAdministration));
		//medicationAdministration.getMeta().setVersionId(getVersionId(openmrsMedicationAdministration));

		return medicationAdministration;
	}

	@Override
	public org.openmrs.module.fhir2.apiext.model.MedicationAdministration toOpenmrsType(@Nonnull MedicationAdministration medicationAdministration) {
		notNull(medicationAdministration, "The MedicationAdministration object should not be null");
		return toOpenmrsType(new org.openmrs.module.fhir2.apiext.model.MedicationAdministration(), medicationAdministration);
	}

	@Override
	public org.openmrs.module.fhir2.apiext.model.MedicationAdministration toOpenmrsType(
	        @Nonnull org.openmrs.module.fhir2.apiext.model.MedicationAdministration existingOpenmrsMedicationAdministration,
	        @Nonnull MedicationAdministration medicationAdministration) {
		notNull(existingOpenmrsMedicationAdministration, "The existing Openmrs Medication Administration object should not be null");
		notNull(medicationAdministration, "The MedicationAdministration object should not be null");

		if (medicationAdministration.hasId()) {
			existingOpenmrsMedicationAdministration.setUuid(medicationAdministration.getIdElement().getIdPart());
		}

		if (medicationAdministration.hasEffectiveDateTimeType()) {
			existingOpenmrsMedicationAdministration
			        .setAdministeredDateTime(medicationAdministration.getEffectiveDateTimeType().getValue());
		}

		if (medicationAdministration.hasStatus()) {
			existingOpenmrsMedicationAdministration
			        .setStatus(statusTranslator.toOpenmrsType(medicationAdministration.getStatus()));
		}

		existingOpenmrsMedicationAdministration
		        .setPatient(patientReferenceTranslator.toOpenmrsType(medicationAdministration.getSubject()));

		if (medicationAdministration.hasContext()) {
			existingOpenmrsMedicationAdministration
			        .setEncounter(encounterReferenceTranslator.toOpenmrsType(medicationAdministration.getContext()));
		}

		if (medicationAdministration.hasPerformer()) {
			MedicationAdministration.MedicationAdministrationPerformerComponent performerComponent = medicationAdministration.getPerformerFirstRep();
			if (performerComponent != null && performerComponent.hasActor()) {
				existingOpenmrsMedicationAdministration.setAdminister(practitionerReferenceTranslator.toOpenmrsType(performerComponent.getActor()));
			} else {
				existingOpenmrsMedicationAdministration.setAdminister(null);
			}
		} else {
			existingOpenmrsMedicationAdministration.setAdminister(null);
		}

		if (medicationAdministration.hasRequest()) {
			existingOpenmrsMedicationAdministration.setDrugOrder(medicationRequestReferenceTranslator.toOpenmrsType(medicationAdministration.getRequest()));
		}

		if (medicationAdministration.hasDosage()) {
			existingOpenmrsMedicationAdministration.setDose(Double.parseDouble(medicationAdministration.getDosage().getDose().getValue()+""));
			//existingOpenmrsMedicationAdministration.setDoseUnits(medicationAdministration.getDosage().getDose().getCode());
			//existingOpenmrsMedicationAdministration.setDrug();
		}

		if (medicationAdministration.hasNote()) {
			existingOpenmrsMedicationAdministration.setNotes(medicationAdministration.getNote().get(0).getText());
		}

		return existingOpenmrsMedicationAdministration;

	}
}
