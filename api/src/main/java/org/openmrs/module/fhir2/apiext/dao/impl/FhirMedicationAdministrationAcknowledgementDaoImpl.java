package org.openmrs.module.fhir2.apiext.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.fhir2.api.dao.impl.BaseFhirDao;
import org.openmrs.module.fhir2.apiext.dao.FhirMedicationAdministrationAcknowledgementDao;
import org.openmrs.module.ipd.api.model.MedicationAdministrationAcknowledgement;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
public class FhirMedicationAdministrationAcknowledgementDaoImpl extends BaseFhirDao<MedicationAdministrationAcknowledgement> implements FhirMedicationAdministrationAcknowledgementDao {

    @Override
    public MedicationAdministrationAcknowledgement get(@Nonnull String uuid) {
        return super.get(uuid);
    }

    @Override
    public boolean hasAcknowledgement(@Nonnull Integer medicationAdministrationId) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(MedicationAdministrationAcknowledgement.class);
        criteria.createAlias("medicationAdministration", "ma");
        criteria.add(Restrictions.eq("ma.medicationAdministrationId", medicationAdministrationId));
        criteria.add(Restrictions.eq("voided", false));
        criteria.add(Restrictions.eq("actionType", "ACKNOWLEDGE"));

        return criteria.uniqueResult() != null;
    }

    @Override
    public MedicationAdministrationAcknowledgement getByMedicationAdministrationId(@Nonnull Integer medicationAdministrationId) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(MedicationAdministrationAcknowledgement.class);
        criteria.createAlias("medicationAdministration", "ma");
        criteria.add(Restrictions.eq("ma.medicationAdministrationId", medicationAdministrationId));
        criteria.add(Restrictions.eq("voided", false));
        criteria.add(Restrictions.eq("actionType", "ACKNOWLEDGE"));

        return (MedicationAdministrationAcknowledgement) criteria.uniqueResult();
    }

}
