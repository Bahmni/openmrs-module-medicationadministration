/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright 2026. CURE International. CURE International is a registered trademark
 * and the CURE International graphic logo is a trademark of CURE International.
 */

package org.openmrs.module.fhir2.apiext.dao.impl;

import org.openmrs.module.fhir2.api.dao.impl.BaseFhirDao;
import org.openmrs.module.fhir2.apiext.dao.FhirMedicationAdministrationPerformerDao;
import org.openmrs.module.ipd.api.model.MedicationAdministrationPerformer;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
public class FhirMedicationAdministrationPerformerDaoImpl extends BaseFhirDao<MedicationAdministrationPerformer> implements FhirMedicationAdministrationPerformerDao {

    @Override
    public MedicationAdministrationPerformer get(@Nonnull String uuid) {
        return super.get(uuid);
    }
}
