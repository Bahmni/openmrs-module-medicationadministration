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

import org.hl7.fhir.r4.model.MedicationAdministration;
import org.openmrs.module.fhir2.api.translators.OpenmrsFhirUpdatableTranslator;

import javax.annotation.Nonnull;

public interface MedicationAdministrationPerformerTranslator<T> extends OpenmrsFhirUpdatableTranslator<T, MedicationAdministration.MedicationAdministrationPerformerComponent> {

    /**
     * Maps <T> an OpenMRS representation of a dispensed medication to a {@link MedicationAdministration.MedicationAdministrationPerformerComponent}
     *
     * @param openmrsObject the OpenMRS representation to translate
     * @return the corresponding FHIR condition resource
     */
    @Override
    MedicationAdministration.MedicationAdministrationPerformerComponent toFhirResource(@Nonnull T openmrsObject);

    /**
     * Maps a {@link MedicationAdministration.MedicationAdministrationPerformerComponent} to an OpenMRS representation
     *
     * @param fhirObject the FHIR condition to translate
     * @return the corresponding OpenMRS representation
     */
    @Override
    T toOpenmrsType(@Nonnull MedicationAdministration.MedicationAdministrationPerformerComponent fhirObject);

    /**
     * Maps a {@link MedicationAdministration.MedicationAdministrationPerformerComponent} to an existing <T> OpenMRS representation
     *
     * @param openmrsObject the existing OpenMRS representation to update
     * @param fhirObject the condition to map
     * @return an updated version of the OpenMRS representation
     */
    @Override
    T toOpenmrsType(@Nonnull T openmrsObject, @Nonnull MedicationAdministration.MedicationAdministrationPerformerComponent fhirObject);

}
