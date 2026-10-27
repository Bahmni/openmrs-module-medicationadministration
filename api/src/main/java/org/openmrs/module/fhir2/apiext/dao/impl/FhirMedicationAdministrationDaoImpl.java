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

import static org.hibernate.criterion.Restrictions.*;

import javax.annotation.Nonnull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import lombok.AccessLevel;
import lombok.Setter;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationStatus;
import org.openmrs.annotation.Authorized;
import org.openmrs.module.ipd.api.model.MedicationAdministration;
import org.openmrs.module.fhir2.FhirConstants;
import org.openmrs.module.fhir2.api.dao.impl.BaseFhirDao;
import org.openmrs.module.fhir2.apiext.dao.FhirMedicationAdministrationDao;
import org.openmrs.module.fhir2.api.search.param.SearchParameterMap;
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
    protected void setupSearchParams(Criteria criteria, SearchParameterMap theParams) {
        theParams.getParameters().forEach(entry -> {
            switch (entry.getKey()) {
                case FhirConstants.ENCOUNTER_REFERENCE_SEARCH_HANDLER:
                    entry.getValue().forEach(
                        e -> handleEncounterReference(criteria, (ReferenceAndListParam) e.getParam(), "e"));
                    break;
                case FhirConstants.PATIENT_REFERENCE_SEARCH_HANDLER:
                    entry.getValue().forEach(
                        p -> handlePatientReference(criteria, (ReferenceAndListParam) p.getParam(), "patient"));
                    break;
                case FhirConstants.MEDICATION_REQUEST_REFERENCE_SEARCH_HANDLER:
                    entry.getValue().forEach(r -> handleMedicationRequestReference("do",
                        (ReferenceAndListParam) r.getParam()).ifPresent(c -> criteria.createAlias("drugOrder", "do").add(c)));
                    break;
                case FhirConstants.MEDICATION_REFERENCE_SEARCH_HANDLER:
                    entry.getValue().forEach(d -> handleMedicationReference("d", (ReferenceAndListParam) d.getParam())
                        .ifPresent(c -> criteria.createAlias("drug", "d").add(c)));
                    break;
                case FhirConstants.STATUS_SEARCH_HANDLER:
                    entry.getValue()
                        .forEach(param -> handleStatus((TokenAndListParam) param.getParam()).ifPresent(criteria::add));
                    break;
                case FhirConstants.MEDICATION_ADMINISTRATION_PERFORMER_SEARCH_HANDLER:
                    entry.getValue().forEach(
                        p -> handlePerformerReference(criteria, (ReferenceAndListParam) p.getParam()));
                    break;
                case FhirConstants.COMMON_SEARCH_HANDLER:
                    handleCommonSearchParameters(entry.getValue()).ifPresent(criteria::add);
                    break;
            }
        });
    }

    private Optional<Criterion> handleStatus(TokenAndListParam tokenAndListParam) {
        return handleAndListParam(tokenAndListParam, token -> {
            if (token.getValue() == null) {
                return Optional.empty();
            }
            try {
                return Optional.of(eq("status", MedicationAdministrationStatus.fromCode(token.getValue().toLowerCase())));
            }
            catch (FHIRException e) {
                return Optional.empty();
            }
        });
    }

    private void handlePerformerReference(Criteria criteria, ReferenceAndListParam reference) {
        if (reference == null) {
            return;
        }
        if (lacksAlias(criteria, "pr")) {
            criteria.createAlias("performers", "pr");
        }
        if (lacksAlias(criteria, "pa")) {
            criteria.createAlias("pr.actor", "pa");
        }
        handleAndListParam(reference, param -> Optional.of(eq("pa.uuid", param.getIdPart()))).ifPresent(criteria::add);
    }
}
