package org.openmrs.module.fhir2.apiext.dao.impl;

import org.openmrs.module.fhir2.api.dao.impl.BaseFhirDao;
import org.openmrs.module.fhir2.apiext.dao.FhirMedicationAdministrationPerformerDao;
import org.openmrs.module.ipd.api.model.MedicationAdministrationPerformer;
import org.springframework.stereotype.Component;

@Component
public class FhirMedicationAdministrationPerformerDaoImpl extends BaseFhirDao<MedicationAdministrationPerformer> implements FhirMedicationAdministrationPerformerDao {
}
