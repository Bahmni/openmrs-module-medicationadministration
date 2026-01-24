package org.openmrs.module.fhir2.apiext.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
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

    @Override
    public MedicationAdministrationNote getLatestNote(@Nonnull Integer medicationAdministrationId) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(MedicationAdministrationNote.class);
        criteria.createAlias("medicationAdministration", "ma");
        criteria.add(Restrictions.eq("ma.medicationAdministrationId", medicationAdministrationId));
        criteria.add(Restrictions.eq("voided", false));
        criteria.addOrder(Order.desc("dateCreated"));
        criteria.setMaxResults(1);

        return (MedicationAdministrationNote) criteria.uniqueResult();
    }

}
