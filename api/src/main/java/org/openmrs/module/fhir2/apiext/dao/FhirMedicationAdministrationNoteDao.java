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

    /**
     * Get the latest (most recent) note for a medication administration.
     * This is used when creating an amendment to link to the previous note.
     *
     * @param medicationAdministrationId the internal ID of the medication administration
     * @return the latest note if exists, null otherwise
     */
    @Authorized(PrivilegeConstants.GET_MEDICATION_ADMINISTRATIONS)
    MedicationAdministrationNote getLatestNote(@Nonnull Integer medicationAdministrationId);
}
