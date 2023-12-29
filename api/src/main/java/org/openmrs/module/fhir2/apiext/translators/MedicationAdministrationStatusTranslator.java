/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhir2.apiext.translators;

import javax.annotation.Nonnull;

import org.hl7.fhir.r4.model.MedicationAdministration;
import org.openmrs.Concept;
import org.openmrs.module.fhir2.api.translators.ToFhirTranslator;
import org.openmrs.module.fhir2.api.translators.ToOpenmrsTranslator;

public interface MedicationAdministrationStatusTranslator extends ToFhirTranslator<Concept, MedicationAdministration.MedicationAdministrationStatus>, ToOpenmrsTranslator<Concept, MedicationAdministration.MedicationAdministrationStatus> {

    @Override
    MedicationAdministration.MedicationAdministrationStatus toFhirResource(@Nonnull Concept concept);

    @Override
    Concept toOpenmrsType(@Nonnull MedicationAdministration.MedicationAdministrationStatus resource);
}
