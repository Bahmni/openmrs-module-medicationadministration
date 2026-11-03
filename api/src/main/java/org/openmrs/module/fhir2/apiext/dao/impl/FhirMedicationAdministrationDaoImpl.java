/*
* This Source Code Form is subject to the terms of the Mozilla Public License,
* v. 2.0. If a copy of the MPL was not distributed with this file, You can
* obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
* the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
*
* Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
* graphic logo is a trademark of OpenMRS Inc.
*/
package org.openmrs.module.fhir2.apiext.dao.impl;

import javax.annotation.Nonnull;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import lombok.AccessLevel;
import lombok.Setter;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationStatus;
import org.openmrs.annotation.Authorized;
import org.openmrs.module.fhir2.FhirConstants;
import org.openmrs.module.fhir2.api.dao.impl.BaseFhirDao;
import org.openmrs.module.fhir2.api.dao.internals.OpenmrsFhirCriteriaContext;
import org.openmrs.module.fhir2.api.search.param.SearchParameterMap;
import org.openmrs.module.fhir2.apiext.dao.FhirMedicationAdministrationDao;
import org.openmrs.module.ipd.api.model.MedicationAdministration;
import org.openmrs.module.medicationadministration.util.PrivilegeConstants;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Setter(AccessLevel.PACKAGE)
public class FhirMedicationAdministrationDaoImpl extends BaseFhirDao<MedicationAdministration> implements FhirMedicationAdministrationDao<MedicationAdministration> {


    @Override
    @Transactional(readOnly = true)
    @Authorized(PrivilegeConstants.GET_MEDICATION_ADMINISTRATIONS)
    public MedicationAdministration get(@Nonnull String uuid) {
        return super.get(uuid);
    }

    @Override
    @Authorized(PrivilegeConstants.EDIT_MEDICATION_ADMINISTRATION)
    public MedicationAdministration createOrUpdate(@Nonnull MedicationAdministration newEntry) {
        return super.createOrUpdate(newEntry);
    }

    @Override
    @Authorized(PrivilegeConstants.DELETE_MEDICATION_ADMINISTRATION)
    public MedicationAdministration delete(@Nonnull String uuid) {
        return super.delete(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    @Authorized(PrivilegeConstants.GET_MEDICATION_ADMINISTRATIONS)
    public List<MedicationAdministration> get(@Nonnull Collection<String> uuids) {
        return super.get(uuids);
    }

    @Override
    @Transactional(readOnly = true)
    @Authorized(PrivilegeConstants.GET_MEDICATION_ADMINISTRATIONS)
    public List<MedicationAdministration> getSearchResults(@Nonnull SearchParameterMap theParams) {
        return super.getSearchResults(theParams);
    }

    @Override
    @Transactional(readOnly = true)
    @Authorized(PrivilegeConstants.GET_MEDICATION_ADMINISTRATIONS)
    public int getSearchResultsCount(@Nonnull SearchParameterMap theParams) {
        return super.getSearchResultsCount(theParams);
    }

    @Override
    protected <U> void setupSearchParams(@Nonnull OpenmrsFhirCriteriaContext<MedicationAdministration, U> criteriaContext,
            @Nonnull SearchParameterMap theParams) {
        theParams.getParameters().forEach(entry -> {
            switch (entry.getKey()) {
                case FhirConstants.ENCOUNTER_REFERENCE_SEARCH_HANDLER:
                    entry.getValue().forEach(
                        e -> getSearchQueryHelper().handleEncounterReference(criteriaContext,
                            (ReferenceAndListParam) e.getParam(), "e"));
                    break;
                case FhirConstants.PATIENT_REFERENCE_SEARCH_HANDLER:
                    entry.getValue().forEach(
                        p -> getSearchQueryHelper().handlePatientReference(criteriaContext,
                            (ReferenceAndListParam) p.getParam(), "patient"));
                    break;
                case FhirConstants.MEDICATION_REQUEST_REFERENCE_SEARCH_HANDLER:
                    From<?, ?> drugOrderJoin = criteriaContext.addJoin("drugOrder", "do");
                    entry.getValue().forEach(r -> getSearchQueryHelper()
                            .handleMedicationRequestReference(criteriaContext, drugOrderJoin,
                                (ReferenceAndListParam) r.getParam())
                            .ifPresent(criteriaContext::addPredicate));
                    break;
                case FhirConstants.MEDICATION_REFERENCE_SEARCH_HANDLER:
                    From<?, ?> drugJoin = criteriaContext.addJoin("drug", "d");
                    entry.getValue().forEach(d -> getSearchQueryHelper()
                            .handleMedicationReference(criteriaContext, drugJoin, (ReferenceAndListParam) d.getParam())
                            .ifPresent(criteriaContext::addPredicate));
                    break;
                case FhirConstants.STATUS_SEARCH_HANDLER:
                    entry.getValue().forEach(param -> handleStatus(criteriaContext, (TokenAndListParam) param.getParam())
                            .ifPresent(criteriaContext::addPredicate));
                    break;
                case FhirConstants.MEDICATION_ADMINISTRATION_PERFORMER_SEARCH_HANDLER:
                    entry.getValue().forEach(
                        p -> handlePerformerReference(criteriaContext, (ReferenceAndListParam) p.getParam()));
                    break;
                case FhirConstants.COMMON_SEARCH_HANDLER:
                    handleCommonSearchParameters(criteriaContext, entry.getValue())
                            .ifPresent(criteriaContext::addPredicate);
                    break;
            }
        });
    }

    private <U> Optional<Predicate> handleStatus(
            OpenmrsFhirCriteriaContext<MedicationAdministration, U> criteriaContext,
            TokenAndListParam tokenAndListParam) {
        return handleAndListParam(criteriaContext.getCriteriaBuilder(), tokenAndListParam, token -> {
            if (token.getValue() == null) {
                return Optional.empty();
            }
            try {
                return Optional.of(criteriaContext.getCriteriaBuilder().equal(criteriaContext.getRoot().get("status"),
                    MedicationAdministrationStatus.fromCode(token.getValue().toLowerCase())));
            }
            catch (FHIRException e) {
                return Optional.empty();
            }
        });
    }

    private <U> void handlePerformerReference(
            OpenmrsFhirCriteriaContext<MedicationAdministration, U> criteriaContext,
            ReferenceAndListParam reference) {
        if (reference == null) {
            return;
        }
        Join<?, ?> performerJoin = criteriaContext.addJoin("performers", "pr");
        Join<?, ?> actorJoin = criteriaContext.addJoin(performerJoin, "actor", "pa");
        handleAndListParam(criteriaContext.getCriteriaBuilder(), reference,
            param -> Optional.of(criteriaContext.getCriteriaBuilder().equal(actorJoin.get("uuid"), param.getIdPart())))
                .ifPresent(criteriaContext::addPredicate);
    }
}
