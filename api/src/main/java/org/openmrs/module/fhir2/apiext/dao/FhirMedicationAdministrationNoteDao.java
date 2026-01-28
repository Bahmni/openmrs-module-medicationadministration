package org.openmrs.module.fhir2.apiext.dao;

import org.openmrs.annotation.Authorized;
import org.openmrs.module.fhir2.api.dao.FhirDao;
import org.openmrs.module.ipd.api.model.MedicationAdministrationNote;
import org.openmrs.util.PrivilegeConstants;

import javax.annotation.Nonnull;

public interface FhirMedicationAdministrationNoteDao extends FhirDao<MedicationAdministrationNote> {

    @Override
    @Authorized(PrivilegeConstants.GET_MEDICATION_ADMINISTRATIONS)
    MedicationAdministrationNote get(@Nonnull String uuid);
}
