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
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.SimpleQuantity;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir2.api.translators.*;
import org.openmrs.module.fhir2.apiext.translators.MedicationAdministrationNoteTranslator;
import org.openmrs.module.fhir2.apiext.translators.MedicationAdministrationPerformerTranslator;
import org.openmrs.module.fhir2.apiext.translators.MedicationAdministrationStatusTranslator;
import org.openmrs.module.fhir2.apiext.translators.MedicationAdministrationTranslator;
import org.openmrs.module.ipd.api.model.MedicationAdministrationNote;
import org.openmrs.module.ipd.api.model.MedicationAdministration;
import org.openmrs.module.ipd.api.model.MedicationAdministrationPerformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
@Setter(AccessLevel.PACKAGE)
public class MedicationAdministrationTranslatorImpl implements MedicationAdministrationTranslator<MedicationAdministration> {

	@Autowired
	private MedicationAdministrationStatusTranslator statusTranslator;

	@Autowired
	private PatientReferenceTranslator patientReferenceTranslator;

	@Autowired
	private EncounterReferenceTranslator<Encounter> encounterReferenceTranslator;

	@Autowired
	private MedicationRequestReferenceTranslator medicationRequestReferenceTranslator;

	@Autowired
	private MedicationReferenceTranslator medicationReferenceTranslator;

	@Autowired
	private MedicationAdministrationPerformerTranslator medicationAdministrationPerformerTranslator;

	@Autowired
	private MedicationAdministrationNoteTranslator medicationAdministrationNoteTranslator;

	@Autowired
	private ConceptTranslator conceptTranslator;



	@Override
	public org.hl7.fhir.r4.model.MedicationAdministration toFhirResource(@Nonnull MedicationAdministration openmrsObject) {
		notNull(openmrsObject, "The MedicationAdministration object should not be null");

		org.hl7.fhir.r4.model.MedicationAdministration fhirObject = new org.hl7.fhir.r4.model.MedicationAdministration();
		fhirObject.setId(openmrsObject.getUuid());
		if (openmrsObject.getAdministeredDateTime() != null) {
			fhirObject.setEffective(new DateTimeType(openmrsObject.getAdministeredDateTime()));
		}
		fhirObject.setStatus(statusTranslator.toFhirResource(openmrsObject.getStatus()));
		if (openmrsObject.getStatusReason() != null) {
			fhirObject.setStatusReason(Collections.singletonList(conceptTranslator.toFhirResource(openmrsObject.getStatusReason())));
		}
		if (openmrsObject.getPatient() != null) {
			fhirObject.setSubject(patientReferenceTranslator.toFhirResource(openmrsObject.getPatient()));
		}
		if (openmrsObject.getEncounter() != null) {
			fhirObject.setContext(encounterReferenceTranslator.toFhirResource(openmrsObject.getEncounter()));
		}

		if (openmrsObject.getPerformers() != null) {
			for (MedicationAdministrationPerformer openmrsMedicationAdministrationPerformer : openmrsObject.getPerformers()) {
				fhirObject.addPerformer(medicationAdministrationPerformerTranslator.toFhirResource(openmrsMedicationAdministrationPerformer));
			}
		}

		if (openmrsObject.getDrugOrder() != null) {
			fhirObject.setRequest(medicationRequestReferenceTranslator.toFhirResource(openmrsObject.getDrugOrder()));
		}

		if (openmrsObject.getDrug() != null) {
			fhirObject.setMedication(medicationReferenceTranslator.toFhirResource(openmrsObject.getDrug()));
		}

		org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationDosageComponent dosage = new org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationDosageComponent();
		if (openmrsObject.getDose() != null && openmrsObject.getDoseUnits() != null) {
			SimpleQuantity dose = new SimpleQuantity();
			dose.setValue(openmrsObject.getDose());
			dose.setUnit(openmrsObject.getDoseUnits().getId().toString());
			dosage.setDose(dose);
		}
		if (openmrsObject.getRoute() != null) {
			dosage.setRoute(conceptTranslator.toFhirResource(openmrsObject.getRoute()));
		}
		if (openmrsObject.getSite() != null) {
			dosage.setSite(conceptTranslator.toFhirResource(openmrsObject.getSite()));
		}
		if (!StringUtils.isEmpty(openmrsObject.getDosingInstructions())) {
			dosage.setText(openmrsObject.getDosingInstructions());
		}
		fhirObject.setDosage(dosage);

		if (openmrsObject.getNotes() != null) {
			for (MedicationAdministrationNote medicationAdministrationNote : openmrsObject.getNotes()) {
				fhirObject.addNote(medicationAdministrationNoteTranslator.toFhirResource(medicationAdministrationNote));
			}
		}

		fhirObject.getMeta().setLastUpdated(getLastUpdated(openmrsObject));
		//medicationAdministration.getMeta().setVersionId(getVersionId(openmrsObject));

		return fhirObject;
	}

	@Override
	public MedicationAdministration toOpenmrsType(@Nonnull org.hl7.fhir.r4.model.MedicationAdministration fhirObject) {
		notNull(fhirObject, "The MedicationAdministration object should not be null");
		return toOpenmrsType(new MedicationAdministration(), fhirObject);
	}

	@Override
	public MedicationAdministration toOpenmrsType(
	        @Nonnull MedicationAdministration openmrsObject,
	        @Nonnull org.hl7.fhir.r4.model.MedicationAdministration fhirObject) {
		notNull(openmrsObject, "The existing Openmrs MedicationAdministration object should not be null");
		notNull(fhirObject, "The Fhir MedicationAdministration object should not be null");

		if (fhirObject.hasId()) {
			openmrsObject.setUuid(fhirObject.getIdElement().getIdPart());
		}
		if (fhirObject.hasEffectiveDateTimeType()) {
			openmrsObject.setAdministeredDateTime(fhirObject.getEffectiveDateTimeType().getValue());
		}
		if (fhirObject.hasStatus()) {
			openmrsObject.setStatus(statusTranslator.toOpenmrsType(fhirObject.getStatus()));
		}
		if (fhirObject.hasStatusReason()) {
			openmrsObject.setStatusReason(conceptTranslator.toOpenmrsType(fhirObject.getStatusReason().get(0)));
		}
		if (fhirObject.hasSubject()) {
			openmrsObject.setPatient(patientReferenceTranslator.toOpenmrsType(fhirObject.getSubject()));
		}
		if (fhirObject.hasContext()) {
			openmrsObject.setEncounter(encounterReferenceTranslator.toOpenmrsType(fhirObject.getContext()));
		}
		if (fhirObject.hasPerformer()) {
			Set<MedicationAdministrationPerformer> performers = new HashSet<>();
			for (org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationPerformerComponent performerComponent : fhirObject.getPerformer()) {
				Object obj = medicationAdministrationPerformerTranslator.toOpenmrsType(performerComponent);
				if (obj != null) {
					performers.add((MedicationAdministrationPerformer) obj);
				}
			}
			openmrsObject.setPerformers(performers);
		}

		if (fhirObject.hasRequest()) {
			openmrsObject.setDrugOrder(medicationRequestReferenceTranslator.toOpenmrsType(fhirObject.getRequest()));
		}
		if (fhirObject.hasMedication()) {
			openmrsObject.setDrug(medicationReferenceTranslator.toOpenmrsType(fhirObject.getMedicationReference()));
		}
		if (fhirObject.hasDosage()) {
			org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationDosageComponent dosage = fhirObject.getDosage();
			if (dosage.hasDose()) {
				openmrsObject.setDose(dosage.getDose().getValue().doubleValue());
				openmrsObject.setDoseUnits(Context.getConceptService().getConcept(dosage.getDose().getUnit()));
			}
			if (dosage.hasRoute()) {
				openmrsObject.setRoute(conceptTranslator.toOpenmrsType(dosage.getRoute()));
			}
			if (dosage.hasSite()) {
				openmrsObject.setSite(conceptTranslator.toOpenmrsType(dosage.getSite()));
			}
			if (dosage.hasText()) {
				openmrsObject.setDosingInstructions(dosage.getText());
			}
		}
		if (fhirObject.hasNote()) {
			Set<MedicationAdministrationNote> medicationAdministrationNotes = new HashSet<>();
			for (org.hl7.fhir.r4.model.Annotation annotation : fhirObject.getNote()) {
				Object obj = medicationAdministrationNoteTranslator.toOpenmrsType(annotation);
				if (obj != null) {
					medicationAdministrationNotes.add((MedicationAdministrationNote) obj);
				}
			}
			openmrsObject.setNotes(medicationAdministrationNotes);
		}
		return openmrsObject;

	}
}
