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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.StringType;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.fhir2.FhirConstants;
import org.openmrs.module.fhir2.api.translators.ConceptTranslator;
import org.openmrs.module.fhir2.apiext.dao.FhirMedicationAdministrationNoteDao;
import org.openmrs.module.ipd.api.model.MedicationAdministrationNote;

public class MedicationAdministrationNoteTranslatorImplTest {

	private static final String NOTE_UUID = "note-uuid";

	private static final String PREVIOUS_UUID = "previous-note-uuid";

	private static final String CONCEPT_UUID = "reason-concept-uuid";

	private MedicationAdministrationNoteTranslatorImpl translator;

	private FhirMedicationAdministrationNoteDao noteDao;

	private ConceptTranslator conceptTranslator;

	private ConceptService conceptService;

	@Before
	public void setup() {
		translator = new MedicationAdministrationNoteTranslatorImpl();
		noteDao = mock(FhirMedicationAdministrationNoteDao.class);
		conceptTranslator = mock(ConceptTranslator.class);
		conceptService = mock(ConceptService.class);
		translator.setFhirMedicationAdministrationNoteDao(noteDao);
		translator.setConceptTranslator(conceptTranslator);
		translator.setConceptService(conceptService);
	}

	private MedicationAdministrationNote note(String uuid) {
		MedicationAdministrationNote note = new MedicationAdministrationNote();
		note.setUuid(uuid);
		note.setText("text");
		return note;
	}

	private Concept concept() {
		Concept concept = new Concept();
		concept.setUuid(CONCEPT_UUID);
		return concept;
	}

	private CodeableConcept codeableConcept() {
		CodeableConcept codeableConcept = new CodeableConcept();
		codeableConcept.addCoding().setCode(CONCEPT_UUID);
		return codeableConcept;
	}

	@Test
	public void toFhirResource_shouldAddBothExtensions() {
		MedicationAdministrationNote note = note(NOTE_UUID);
		note.setPreviousNote(note(PREVIOUS_UUID));
		note.setStatusReason(concept());
		when(conceptTranslator.toFhirResource(any(Concept.class))).thenReturn(codeableConcept());

		Annotation annotation = translator.toFhirResource(note);

		Extension previous = annotation.getExtensionByUrl(FhirConstants.NOTE_PREVIOUS_NOTE_ID_EXTENSION_URL);
		assertEquals(PREVIOUS_UUID, ((StringType) previous.getValue()).getValue());
		Extension reason = annotation.getExtensionByUrl(FhirConstants.NOTE_AMENDMENT_REASON_EXTENSION_URL);
		assertEquals(CONCEPT_UUID, ((CodeableConcept) reason.getValue()).getCodingFirstRep().getCode());
	}

	@Test
	public void toFhirResource_shouldNotAddExtensionsWhenNull() {
		Annotation annotation = translator.toFhirResource(note(NOTE_UUID));

		assertTrue(annotation.getExtension().isEmpty());
	}

	@Test
	public void toOpenmrsType_shouldReadBothExtensions() {
		MedicationAdministrationNote previous = note(PREVIOUS_UUID);
		Concept concept = concept();
		when(noteDao.get(PREVIOUS_UUID)).thenReturn(previous);
		when(conceptService.getConceptByUuid(CONCEPT_UUID)).thenReturn(concept);
		Annotation annotation = new Annotation();
		annotation.addExtension(new Extension(FhirConstants.NOTE_PREVIOUS_NOTE_ID_EXTENSION_URL, new StringType(PREVIOUS_UUID)));
		annotation.addExtension(new Extension(FhirConstants.NOTE_AMENDMENT_REASON_EXTENSION_URL, codeableConcept()));

		MedicationAdministrationNote result = translator.toOpenmrsType(new MedicationAdministrationNote(), annotation);

		assertEquals(previous, result.getPreviousNote());
		assertEquals(concept, result.getStatusReason());
	}

	@Test
	public void toOpenmrsType_shouldLeaveFieldsNullWithoutExtensions() {
		MedicationAdministrationNote result = translator.toOpenmrsType(new MedicationAdministrationNote(), new Annotation());

		assertNull(result.getPreviousNote());
		assertNull(result.getStatusReason());
	}

	@Test
	public void toOpenmrsType_shouldIgnoreUnknownPreviousNoteUuid() {
		when(noteDao.get("unknown")).thenReturn(null);
		Annotation annotation = new Annotation();
		annotation.addExtension(new Extension(FhirConstants.NOTE_PREVIOUS_NOTE_ID_EXTENSION_URL, new StringType("unknown")));

		MedicationAdministrationNote result = translator.toOpenmrsType(new MedicationAdministrationNote(), annotation);

		assertNull(result.getPreviousNote());
	}

	@Test
	public void toOpenmrsType_shouldIgnoreBlankPreviousNoteUuid() {
		Annotation annotation = new Annotation();
		annotation.addExtension(new Extension(FhirConstants.NOTE_PREVIOUS_NOTE_ID_EXTENSION_URL, new StringType(" ")));

		assertNull(translator.toOpenmrsType(new MedicationAdministrationNote(), annotation).getPreviousNote());
	}

	@Test
	public void roundTrip_shouldPreserveExtensions() {
		MedicationAdministrationNote previous = note(PREVIOUS_UUID);
		Concept concept = concept();
		MedicationAdministrationNote note = note(NOTE_UUID);
		note.setPreviousNote(previous);
		note.setStatusReason(concept);
		when(conceptTranslator.toFhirResource(concept)).thenReturn(codeableConcept());
		when(noteDao.get(PREVIOUS_UUID)).thenReturn(previous);
		when(conceptService.getConceptByUuid(CONCEPT_UUID)).thenReturn(concept);

		Annotation annotation = translator.toFhirResource(note);
		MedicationAdministrationNote result = translator.toOpenmrsType(new MedicationAdministrationNote(), annotation);

		assertNotNull(result.getPreviousNote());
		assertEquals(PREVIOUS_UUID, result.getPreviousNote().getUuid());
		assertEquals(concept, result.getStatusReason());
	}
}
