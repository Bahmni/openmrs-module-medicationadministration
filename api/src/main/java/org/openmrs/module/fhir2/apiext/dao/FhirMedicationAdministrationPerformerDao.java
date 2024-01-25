package org.openmrs.module.fhir2.apiext.dao;

import org.openmrs.annotation.Authorized;
import org.openmrs.module.fhir2.api.dao.FhirDao;
import org.openmrs.module.ipd.api.model.MedicationAdministrationPerformer;
import org.openmrs.util.PrivilegeConstants;

import javax.annotation.Nonnull;

public interface FhirMedicationAdministrationPerformerDao extends FhirDao<MedicationAdministrationPerformer> {

    @Override
    @Authorized(PrivilegeConstants.GET_MEDICATION_ADMINISTRATIONS)
    MedicationAdministrationPerformer get(@Nonnull String uuid);
}
