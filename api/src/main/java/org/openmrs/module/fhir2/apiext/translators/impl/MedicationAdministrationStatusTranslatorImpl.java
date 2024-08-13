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

import lombok.AccessLevel;
import lombok.Setter;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.openmrs.ConceptSource;
import org.openmrs.module.fhir2.api.FhirConceptSourceService;
import org.openmrs.module.fhir2.apiext.translators.MedicationAdministrationStatusTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Optional;

@Component
@Setter(AccessLevel.PACKAGE)
public class MedicationAdministrationStatusTranslatorImpl implements MedicationAdministrationStatusTranslator {

    public static final String CONCEPT_SOURCE_URI = "http://hl7.org/fhir/CodeSystem/medication-admin-status";

    @Autowired
    FhirConceptSourceService conceptSourceService;

    @Override
    public MedicationAdministration.MedicationAdministrationStatus toFhirResource(@Nonnull String concept) {
        Optional<ConceptSource> conceptSource = conceptSourceService.getConceptSourceByUrl(CONCEPT_SOURCE_URI);
        if (conceptSource.isPresent()) {
            return MedicationAdministration.MedicationAdministrationStatus.fromCode(concept);
        }
        return null;
    }

    @Override
    public String toOpenmrsType(@Nonnull MedicationAdministration.MedicationAdministrationStatus status) {
        String concept;
        concept = status.toCode();
        return concept;
    }
}
