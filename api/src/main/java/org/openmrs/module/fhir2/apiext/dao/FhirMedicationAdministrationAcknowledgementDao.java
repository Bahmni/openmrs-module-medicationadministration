package org.openmrs.module.fhir2.apiext.dao;

import org.openmrs.annotation.Authorized;
import org.openmrs.module.fhir2.api.dao.FhirDao;
import org.openmrs.module.ipd.api.model.MedicationAdministrationAcknowledgement;
import org.openmrs.util.PrivilegeConstants;

import javax.annotation.Nonnull;

public interface FhirMedicationAdministrationAcknowledgementDao extends FhirDao<MedicationAdministrationAcknowledgement> {

    @Override
    @Authorized(PrivilegeConstants.GET_MEDICATION_ADMINISTRATIONS)
    MedicationAdministrationAcknowledgement get(@Nonnull String uuid);

    /**
     * Check if a medication administration has any un-voided acknowledgements
     *
     * @param medicationAdministrationId the internal ID of the medication administration
     * @return true if an acknowledgement exists, false otherwise
     */
    @Authorized(PrivilegeConstants.GET_MEDICATION_ADMINISTRATIONS)
    boolean hasAcknowledgement(@Nonnull Integer medicationAdministrationId);

    /**
     * Get the acknowledgement for a specific medication administration
     *
     * @param medicationAdministrationId the internal ID of the medication administration
     * @return the acknowledgement if exists, null otherwise
     */
    @Authorized(PrivilegeConstants.GET_MEDICATION_ADMINISTRATIONS)
    MedicationAdministrationAcknowledgement getByMedicationAdministrationId(@Nonnull Integer medicationAdministrationId);
}
