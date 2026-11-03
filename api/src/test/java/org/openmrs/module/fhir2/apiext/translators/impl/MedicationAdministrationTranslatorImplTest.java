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
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.SimpleQuantity;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.module.fhir2.api.translators.impl.MedicationQuantityCodingTranslatorImpl;
import org.openmrs.module.ipd.api.model.MedicationAdministration;

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
