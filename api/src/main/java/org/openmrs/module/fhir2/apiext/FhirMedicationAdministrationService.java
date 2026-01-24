/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.fhir2.apiext;

import ca.uhn.fhir.rest.api.server.IBundleProvider;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.openmrs.module.fhir2.api.FhirService;
import org.openmrs.module.fhir2.apiext.search.param.MedicationAdministrationSearchParams;
import org.openmrs.module.ipd.api.model.MedicationAdministrationAcknowledgement;
import org.openmrs.module.ipd.api.model.MedicationAdministrationNote;

import javax.annotation.Nonnull;

public interface FhirMedicationAdministrationService extends FhirService<MedicationAdministration> {

	IBundleProvider searchForMedicationAdministration(
	        MedicationAdministrationSearchParams medicationAdministrationSearchParams);

	/**
	 * Add an amendment note to a medication administration record.
	 * Creates a new note that links to the previous note (if exists) using a linked list pattern.
	 *
	 * @param medicationAdministrationUuid the UUID of the medication administration
	 * @param text the note text content
	 * @param amendmentReason optional reason for the amendment (e.g., "Incorrect Dose")
	 * @return the created note
	 * @throws org.openmrs.api.APIException if the record is locked (acknowledged)
	 */
	MedicationAdministrationNote amendNote(@Nonnull String medicationAdministrationUuid,
	                                       @Nonnull String text,
	                                       String amendmentReason);

	/**
	 * Acknowledge (approve/verify) a medication administration record.
	 * Once acknowledged, the record is locked and no further amendments are allowed.
	 *
	 * @param medicationAdministrationUuid the UUID of the medication administration
	 * @param remarks optional remarks/comments from the acknowledger
	 * @return the created acknowledgement
	 * @throws org.openmrs.api.APIException if already acknowledged
	 */
	MedicationAdministrationAcknowledgement acknowledge(@Nonnull String medicationAdministrationUuid,
	                                                     String remarks);

	/**
	 * Check if a medication administration is locked from amendments.
	 *
	 * @param medicationAdministrationUuid the UUID of the medication administration
	 * @return true if acknowledged and locked, false otherwise
	 */
	boolean isLocked(@Nonnull String medicationAdministrationUuid);
}
