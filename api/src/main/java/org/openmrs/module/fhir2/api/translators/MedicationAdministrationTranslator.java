/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhir2.api.translators;

import javax.annotation.Nonnull;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.openmrs.module.fhir2.model.FhirMedicationAdministration;

public interface MedicationAdministrationTranslator<T> extends OpenmrsFhirUpdatableTranslator<T, MedicationAdministration> {
	
	/**
	 * Maps a {@link T} to a {@link MedicationAdministration} resource
	 *
	 * @param medicationAdministrationData the OpenMRS drugOrder to translate
	 * @return the corresponding FHIR MedicationAdministration resource
	 */
	@Override
	MedicationAdministration toFhirResource(@Nonnull T medicationAdministrationData);
	
	/**
	 * Maps a {@link MedicationAdministration} medicationAdministration to an existing
	 * {@link FhirMedicationAdministration}
	 *
	 * @param existingMedicationAdministrationData the existingDrugOrder to update
	 * @param medicationAdministration the medicationAdministration to map
	 * @return an updated version of the existingDrugOrder
	 */
	@Override
	T toOpenmrsType(@Nonnull T existingMedicationAdministrationData,
	        @Nonnull MedicationAdministration medicationAdministration);
	
	/**
	 * Maps a {@link MedicationAdministration} medicationAdministration to an existing
	 * {@link T}
	 *
	 * @param medicationAdministration the medicationAdministration to map
	 * @return an updated version of the existingDrugOrder
	 */
	@Override
	T toOpenmrsType(@Nonnull MedicationAdministration medicationAdministration);
	
}
