package org.openmrs.module.fhir2.apiext.dao.impl;

import org.openmrs.module.fhir2.api.dao.impl.BaseFhirDao;
import org.openmrs.module.fhir2.apiext.dao.FhirMedicationAdministrationNoteDao;
import org.openmrs.module.ipd.api.model.MedicationAdministrationNote;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
public class FhirMedicationAdministrationNoteDaoImpl extends BaseFhirDao<MedicationAdministrationNote> implements FhirMedicationAdministrationNoteDao {

    @Override
    public MedicationAdministrationNote get(@Nonnull String uuid) {
        return super.get(uuid);
    }

}
