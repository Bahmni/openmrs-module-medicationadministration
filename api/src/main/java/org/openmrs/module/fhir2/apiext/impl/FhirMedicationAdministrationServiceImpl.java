/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhir2.apiext.impl;

import ca.uhn.fhir.rest.api.server.IBundleProvider;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.openmrs.module.fhir2.api.impl.BaseFhirService;
import org.openmrs.module.fhir2.apiext.FhirMedicationAdministrationService;
import org.openmrs.module.fhir2.apiext.dao.FhirMedicationAdministrationDao;
import org.openmrs.module.fhir2.api.search.SearchQuery;
import org.openmrs.module.fhir2.api.search.SearchQueryInclude;
import org.openmrs.module.fhir2.apiext.search.param.MedicationAdministrationSearchParams;
import org.openmrs.module.fhir2.apiext.translators.MedicationAdministrationTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Setter(AccessLevel.PACKAGE)
@Getter(AccessLevel.PROTECTED)
public class FhirMedicationAdministrationServiceImpl extends BaseFhirService<MedicationAdministration, org.openmrs.module.ipd.api.model.MedicationAdministration> implements FhirMedicationAdministrationService {

	@Autowired
	private MedicationAdministrationTranslator translator;

	@Autowired
	private FhirMedicationAdministrationDao<org.openmrs.module.ipd.api.model.MedicationAdministration> dao;

	@Autowired
	private SearchQueryInclude<MedicationAdministration> searchQueryInclude;

	@Autowired
	private SearchQuery<org.openmrs.module.ipd.api.model.MedicationAdministration,MedicationAdministration,FhirMedicationAdministrationDao<org.openmrs.module.ipd.api.model.MedicationAdministration>, MedicationAdministrationTranslator<org.openmrs.module.ipd.api.model.MedicationAdministration>, SearchQueryInclude<MedicationAdministration>> searchQuery;

	@Autowired
	private org.openmrs.module.fhir2.apiext.dao.FhirMedicationAdministrationNoteDao noteDao;

	@Autowired
	private org.openmrs.module.fhir2.apiext.dao.FhirMedicationAdministrationAcknowledgementDao acknowledgementDao;

	@Override
	public IBundleProvider searchForMedicationAdministration(
	        MedicationAdministrationSearchParams medicationAdministrationSearchParams) {
		return searchQuery.getQueryResults(medicationAdministrationSearchParams.toSearchParameterMap(), dao, translator,
	    searchQueryInclude);
	}

	@Override
	@org.springframework.transaction.annotation.Transactional
	public org.openmrs.module.ipd.api.model.MedicationAdministrationNote amendNote(String medicationAdministrationUuid,
	                                                                                 String text,
	                                                                                 String amendmentReason) {
		if (medicationAdministrationUuid == null) {
			throw new org.openmrs.api.APIException("Medication administration UUID cannot be null");
		}
		if (text == null || text.trim().isEmpty()) {
			throw new org.openmrs.api.APIException("Note text cannot be null or empty");
		}

		// Get the medication administration
		org.openmrs.module.ipd.api.model.MedicationAdministration medAdmin = dao.get(medicationAdministrationUuid);
		if (medAdmin == null) {
			throw new org.openmrs.api.APIException("Medication administration not found with UUID: " + medicationAdministrationUuid);
		}

		// Check if locked (acknowledged)
		if (isLocked(medicationAdministrationUuid)) {
			throw new org.openmrs.api.APIException("Cannot amend: medication administration is acknowledged and locked");
		}

		// Get the latest note (if exists)
		org.openmrs.module.ipd.api.model.MedicationAdministrationNote latestNote =
			noteDao.getLatestNote(medAdmin.getId());

		// Create new note
		org.openmrs.module.ipd.api.model.MedicationAdministrationNote newNote =
			new org.openmrs.module.ipd.api.model.MedicationAdministrationNote();
		newNote.setUuid(java.util.UUID.randomUUID().toString());
		newNote.setText(text);
		newNote.setAmendmentReason(amendmentReason);
		newNote.setPreviousNote(latestNote);
		newNote.setRecordedTime(new java.util.Date());

		// Set author from authenticated user's provider
		org.openmrs.User authenticatedUser = org.openmrs.api.context.Context.getAuthenticatedUser();
		if (authenticatedUser != null && authenticatedUser.getPerson() != null) {
			java.util.Collection<org.openmrs.Provider> providers =
				org.openmrs.api.context.Context.getProviderService().getProvidersByPerson(authenticatedUser.getPerson());
			if (!providers.isEmpty()) {
				newNote.setAuthor(providers.iterator().next());
			}
		}

		return noteDao.createOrUpdate(newNote);
	}

	@Override
	@org.springframework.transaction.annotation.Transactional
	public org.openmrs.module.ipd.api.model.MedicationAdministrationAcknowledgement acknowledge(
			String medicationAdministrationUuid,
			String remarks) {
		if (medicationAdministrationUuid == null) {
			throw new org.openmrs.api.APIException("Medication administration UUID cannot be null");
		}

		// Get the medication administration
		org.openmrs.module.ipd.api.model.MedicationAdministration medAdmin = dao.get(medicationAdministrationUuid);
		if (medAdmin == null) {
			throw new org.openmrs.api.APIException("Medication administration not found with UUID: " + medicationAdministrationUuid);
		}

		// Check if already acknowledged (single acknowledgement only)
		if (isLocked(medicationAdministrationUuid)) {
			throw new org.openmrs.api.APIException("Medication administration is already acknowledged");
		}

		// Create acknowledgement
		org.openmrs.module.ipd.api.model.MedicationAdministrationAcknowledgement ack =
			new org.openmrs.module.ipd.api.model.MedicationAdministrationAcknowledgement();
		ack.setUuid(java.util.UUID.randomUUID().toString());
		ack.setMedicationAdministration(medAdmin);
		ack.setActionType("ACKNOWLEDGE");
		ack.setActionDatetime(new java.util.Date());
		ack.setRemarks(remarks);

		// Set provider from authenticated user
		org.openmrs.User authenticatedUser = org.openmrs.api.context.Context.getAuthenticatedUser();
		if (authenticatedUser != null && authenticatedUser.getPerson() != null) {
			java.util.Collection<org.openmrs.Provider> providers =
				org.openmrs.api.context.Context.getProviderService().getProvidersByPerson(authenticatedUser.getPerson());
			if (!providers.isEmpty()) {
				ack.setProvider(providers.iterator().next());
			} else {
				throw new org.openmrs.api.APIException("Authenticated user must have an associated provider to acknowledge");
			}
		} else {
			throw new org.openmrs.api.APIException("Authenticated user must have an associated provider to acknowledge");
		}

		return acknowledgementDao.createOrUpdate(ack);
	}

	@Override
	@org.springframework.transaction.annotation.Transactional(readOnly = true)
	public boolean isLocked(String medicationAdministrationUuid) {
		if (medicationAdministrationUuid == null) {
			return false;
		}

		org.openmrs.module.ipd.api.model.MedicationAdministration medAdmin = dao.get(medicationAdministrationUuid);
		if (medAdmin == null) {
			return false;
		}

		return acknowledgementDao.hasAcknowledgement(medAdmin.getId());
	}

}
