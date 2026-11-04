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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.SimpleQuantity;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.module.fhir2.api.translators.ConceptTranslator;
import org.openmrs.module.fhir2.api.translators.MedicationReferenceTranslator;
import org.openmrs.module.fhir2.api.translators.MedicationRequestReferenceTranslator;
import org.openmrs.module.fhir2.api.translators.impl.MedicationQuantityCodingTranslatorImpl;
import org.openmrs.module.fhir2.apiext.translators.MedicationAdministrationPerformerTranslator;
import org.openmrs.module.ipd.api.model.MedicationAdministration;
import org.openmrs.module.ipd.api.model.MedicationAdministrationPerformer;

public class MedicationAdministrationTranslatorImplTest {

	private static final String DOSE_UNIT_NAME = "Tablet";

	private static final String DOSE_UNIT_CODE = "428641000";

	private static final String DOSE_UNIT_SYSTEM = "http://snomed.info/sct";

	private MedicationAdministrationTranslatorImpl translator;

	private MedicationQuantityCodingTranslatorImpl quantityCodingTranslator;

	@Before
	public void setup() {
		translator = new MedicationAdministrationTranslatorImpl();
		quantityCodingTranslator = mock(MedicationQuantityCodingTranslatorImpl.class);
		translator.setQuantityCodingTranslator(quantityCodingTranslator);
		translator.setMedicationRequestReferenceTranslator(mock(MedicationRequestReferenceTranslator.class));
	}

	@Test
	public void toFhirResource_shouldWriteHumanReadableDoseUnitWithCoding() {
		Concept doseUnits = mock(Concept.class);
		when(doseUnits.getDisplayString()).thenReturn(DOSE_UNIT_NAME);
		when(quantityCodingTranslator.toFhirResource(doseUnits))
		        .thenReturn(new Coding(DOSE_UNIT_SYSTEM, DOSE_UNIT_CODE, DOSE_UNIT_NAME));

		MedicationAdministration medicationAdministration = new MedicationAdministration();
		medicationAdministration.setDose(1.0);
		medicationAdministration.setDoseUnits(doseUnits);

		org.hl7.fhir.r4.model.MedicationAdministration fhirResource = translator
		        .toFhirResource(medicationAdministration);

		Quantity dose = fhirResource.getDosage().getDose();
		assertNotNull(dose);
		assertEquals(1.0, dose.getValue().doubleValue(), 0.001);
		assertEquals(DOSE_UNIT_NAME, dose.getUnit());
		assertEquals(DOSE_UNIT_CODE, dose.getCode());
		assertEquals(DOSE_UNIT_SYSTEM, dose.getSystem());
	}

	@Test
	public void toFhirResource_shouldDeriveMedicationFromDrugOrderDrug() {
		Drug drug = mock(Drug.class);
		DrugOrder drugOrder = mock(DrugOrder.class);
		when(drugOrder.getDrug()).thenReturn(drug);
		MedicationReferenceTranslator medicationReferenceTranslator = mock(MedicationReferenceTranslator.class);
		Reference reference = new Reference("Medication/drug-uuid");
		when(medicationReferenceTranslator.toFhirResource(drug)).thenReturn(reference);
		translator.setMedicationReferenceTranslator(medicationReferenceTranslator);

		MedicationAdministration medicationAdministration = new MedicationAdministration();
		medicationAdministration.setDrugOrder(drugOrder);

		org.hl7.fhir.r4.model.MedicationAdministration fhirResource = translator
		        .toFhirResource(medicationAdministration);

		assertEquals(reference, fhirResource.getMedicationReference());
	}

	@Test
	public void toFhirResource_shouldFallBackToOrderConceptWhenOrderHasNoDrug() {
		Concept concept = mock(Concept.class);
		DrugOrder drugOrder = mock(DrugOrder.class);
		when(drugOrder.getDrug()).thenReturn(null);
		when(drugOrder.getConcept()).thenReturn(concept);
		ConceptTranslator conceptTranslator = mock(ConceptTranslator.class);
		CodeableConcept codeableConcept = new CodeableConcept().setText("Paracetamol");
		when(conceptTranslator.toFhirResource(concept)).thenReturn(codeableConcept);
		translator.setConceptTranslator(conceptTranslator);

		MedicationAdministration medicationAdministration = new MedicationAdministration();
		medicationAdministration.setDrugOrder(drugOrder);

		org.hl7.fhir.r4.model.MedicationAdministration fhirResource = translator
		        .toFhirResource(medicationAdministration);

		assertEquals(codeableConcept, fhirResource.getMedicationCodeableConcept());
	}

	@Test
	public void toOpenmrsType_shouldNotResolveDrugFromCodeableConcept() {
		MedicationReferenceTranslator medicationReferenceTranslator = mock(MedicationReferenceTranslator.class);
		translator.setMedicationReferenceTranslator(medicationReferenceTranslator);

		org.hl7.fhir.r4.model.MedicationAdministration fhirResource = new org.hl7.fhir.r4.model.MedicationAdministration();
		fhirResource.setMedication(new CodeableConcept().setText("Paracetamol"));

		MedicationAdministration medicationAdministration = translator.toOpenmrsType(fhirResource);

		assertNull(medicationAdministration.getDrug());
		verifyNoInteractions(medicationReferenceTranslator);
	}

	@Test
	public void toOpenmrsType_shouldResolveDrugFromMedicationReference() {
		Drug drug = mock(Drug.class);
		MedicationReferenceTranslator medicationReferenceTranslator = mock(MedicationReferenceTranslator.class);
		Reference reference = new Reference("Medication/drug-uuid");
		when(medicationReferenceTranslator.toOpenmrsType(reference)).thenReturn(drug);
		translator.setMedicationReferenceTranslator(medicationReferenceTranslator);

		org.hl7.fhir.r4.model.MedicationAdministration fhirResource = new org.hl7.fhir.r4.model.MedicationAdministration();
		fhirResource.setMedication(reference);

		MedicationAdministration medicationAdministration = translator.toOpenmrsType(fhirResource);

		assertEquals(drug, medicationAdministration.getDrug());
	}

	@Test
	public void toOpenmrsType_shouldNotPersistDerivedMedicationForScheduledAdministration() {
		MedicationReferenceTranslator medicationReferenceTranslator = mock(MedicationReferenceTranslator.class);
		translator.setMedicationReferenceTranslator(medicationReferenceTranslator);

		org.hl7.fhir.r4.model.MedicationAdministration fhirResource = new org.hl7.fhir.r4.model.MedicationAdministration();
		fhirResource.setMedication(new Reference("Medication/drug-uuid"));
		fhirResource.setRequest(new Reference("MedicationRequest/order-uuid"));

		MedicationAdministration medicationAdministration = translator.toOpenmrsType(fhirResource);

		assertNull(medicationAdministration.getDrug());
		verifyNoInteractions(medicationReferenceTranslator);
	}

	@Test
	public void toOpenmrsType_shouldReplacePerformersInPlace() {
		MedicationAdministrationPerformer oldPerformer = new MedicationAdministrationPerformer();
		MedicationAdministration existing = new MedicationAdministration();
		existing.setPerformers(new HashSet<>(Collections.singleton(oldPerformer)));
		java.util.Set<MedicationAdministrationPerformer> originalCollection = existing.getPerformers();

		MedicationAdministrationPerformer newPerformer = new MedicationAdministrationPerformer();
		MedicationAdministrationPerformerTranslator performerTranslator = mock(
		        MedicationAdministrationPerformerTranslator.class);
		when(performerTranslator.toOpenmrsType(any())).thenReturn(newPerformer);
		translator.setMedicationAdministrationPerformerTranslator(performerTranslator);

		org.hl7.fhir.r4.model.MedicationAdministration fhirResource = new org.hl7.fhir.r4.model.MedicationAdministration();
		fhirResource.addPerformer().setActor(new Reference("Practitioner/provider-uuid"));

		MedicationAdministration result = translator.toOpenmrsType(existing, fhirResource);

		// Hibernate requires the persistent collection instance to be kept (delete-orphan).
		assertSame(originalCollection, result.getPerformers());
		assertTrue(result.getPerformers().contains(newPerformer));
		assertFalse(result.getPerformers().contains(oldPerformer));
	}

	@Test
	public void toOpenmrsType_shouldResolveDoseUnitFromQuantityCoding() {
		Concept doseUnits = mock(Concept.class);
		when(quantityCodingTranslator.toOpenmrsType(org.mockito.ArgumentMatchers.any(Quantity.class)))
		        .thenReturn(doseUnits);

		org.hl7.fhir.r4.model.MedicationAdministration fhirResource = new org.hl7.fhir.r4.model.MedicationAdministration();
		SimpleQuantity dose = new SimpleQuantity();
		dose.setValue(1.0);
		dose.setUnit(DOSE_UNIT_NAME);
		dose.setCode(DOSE_UNIT_CODE);
		dose.setSystem(DOSE_UNIT_SYSTEM);
		fhirResource.getDosage().setDose(dose);

		MedicationAdministration medicationAdministration = translator.toOpenmrsType(fhirResource);

		assertEquals(1.0, medicationAdministration.getDose(), 0.001);
		assertEquals(doseUnits, medicationAdministration.getDoseUnits());
	}
}
