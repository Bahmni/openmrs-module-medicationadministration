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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import lombok.AccessLevel;
import lombok.Setter;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.openmrs.module.fhir2.apiext.model.MedicationAdministration;
import org.openmrs.module.fhir2.FhirConstants;
import org.openmrs.module.fhir2.api.dao.impl.BaseFhirDao;
import org.openmrs.module.fhir2.apiext.dao.FhirMedicationAdministrationDao;
import org.openmrs.module.fhir2.api.search.param.SearchParameterMap;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Setter(AccessLevel.PACKAGE)
public class FhirMedicationAdministrationDaoImpl extends BaseFhirDao<MedicationAdministration> implements FhirMedicationAdministrationDao<MedicationAdministration> {


 @Override
 @Transactional(readOnly = true)
 // TODO Add Privilege constants
 public MedicationAdministration get(@Nonnull String uuid) {
  return super.get(uuid);
 }

 @Override
 // TODO Add Privilege constants
 public MedicationAdministration createOrUpdate(@Nonnull MedicationAdministration newEntry) {
  return super.createOrUpdate(newEntry);
 }

 @Override
 // TODO Add Privilege constants
 public MedicationAdministration delete(@Nonnull String uuid) {
  return super.delete(uuid);
 }

 @Override
 protected void setupSearchParams(Criteria criteria, SearchParameterMap theParams) {
  theParams.getParameters().forEach(entry -> {
   switch (entry.getKey()) {
    case FhirConstants.ENCOUNTER_REFERENCE_SEARCH_HANDLER:
     entry.getValue()
             .forEach(e -> handleEncounterReference(criteria, (ReferenceAndListParam) e.getParam(), "e"));
     break;
    case FhirConstants.PATIENT_REFERENCE_SEARCH_HANDLER:
     entry.getValue().forEach(patientReference -> handleReference(criteria,
             (ReferenceAndListParam) patientReference.getParam(), "subjectReference", "s"));
     break;
//    case FhirConstants.MEDICATION_ADMINISTRATION_SUPPORT_INFO_SEARCH_HANDLER:
//     entry.getValue().forEach(supportInfoReference -> handleReference(criteria,
//             (ReferenceAndListParam) supportInfoReference.getParam(), "supportingInformation", "si"));
//     break;
//    case FhirConstants.MEDICATION_ADMINISTRATION_PERFORMER_SEARCH_HANDLER:
//     entry.getValue()
//             .forEach(participantReference -> handleMedicationAdministrationPerformerReference(criteria,
//                     (ReferenceAndListParam) participantReference.getParam(), "actorReference", "a"));
//     break;
    case FhirConstants.MEDICATION_REFERENCE_SEARCH_HANDLER:
     entry.getValue().forEach(d -> handleMedicationReference("d", (ReferenceAndListParam) d.getParam())
             .ifPresent(c -> criteria.createAlias("drug", "d").add(c)));
     break;
    case FhirConstants.STATUS_SEARCH_HANDLER:
     entry.getValue()
             .forEach(param -> handleStatus((TokenAndListParam) param.getParam()).ifPresent(criteria::add));
    case FhirConstants.COMMON_SEARCH_HANDLER:
     handleCommonSearchParameters(entry.getValue()).ifPresent(criteria::add);
     break;
   }
  });
 }

 private Optional<Criterion> handleStatus(TokenAndListParam tokenAndListParam) {
  return handleAndListParam(tokenAndListParam, token -> {
   if (token.getValue() != null) {
    try {
     // currently only handles "ACTIVE"
     if (MedicationRequest.MedicationRequestStatus.ACTIVE.toString().equals(token.getValue().toUpperCase())) {
      return Optional.of(generateActiveOrderQuery());
     }
    }
    catch (IllegalArgumentException e) {
     return Optional.empty();
    }
   }

   return Optional.empty();
  });
 }

 private void handleCodedConcept(Criteria criteria, TokenAndListParam code) {
  if (code != null) {
   if (lacksAlias(criteria, "c")) {
    criteria.createAlias("concept", "c");
   }

   handleCodeableConcept(criteria, code, "c", "cm", "crt").ifPresent(criteria::add);
  }
 }

 private void handleReference(Criteria criteria, ReferenceAndListParam reference, String property, String alias) {
  handleAndListParam(reference, param -> {
   if (validReferenceParam(param)) {
    if (lacksAlias(criteria, alias)) {
     criteria.createAlias(property, alias);
    }

    List<Optional<Criterion>> criterionList = new ArrayList<>();
    criterionList.add(Optional.of(eq(String.format("%s.targetUuid", alias), param.getIdPart())));
    criterionList.add(Optional.of(eq(String.format("%s.type", alias), param.getResourceType())));
    return Optional.of(and(toCriteriaArray(criterionList)));
   }

   return Optional.empty();
  }).ifPresent(criteria::add);
 }

 private void handleMedicationAdministrationPerformerReference(Criteria criteria, ReferenceAndListParam reference,
                                                               String property, String alias) {
  if (lacksAlias(criteria, "pr")) {
   criteria.createAlias("performer", "pr");
  }

  handleAndListParam(reference, param -> {
   if (validReferenceParam(param)) {
    if (lacksAlias(criteria, alias)) {
     criteria.createAlias(property, alias);
    }

    List<Optional<Criterion>> criterionList = new ArrayList<>();
    criterionList.add(Optional.of(eq(String.format("%s.targetUuid", alias), param.getIdPart())));
    criterionList.add(Optional.of(eq(String.format("%s.type", alias), param.getResourceType())));
    return Optional.of(and(toCriteriaArray(criterionList)));
   }

   return Optional.empty();
  }).ifPresent(criteria::add);
 }
 //
 //	private void handleSupportingInformationReference(Criteria criteria, ReferenceAndListParam reference,
 //																  String property, String alias) {
 //		if (lacksAlias(criteria, "si")) {
 //			criteria.createAlias("supportingInformation", "supportingInformation");
 //		}
 //		criteria.add(eq("si."))
 //
 //		handleAndListParam(reference, param -> {
 //			if (validReferenceParam(param)) {
 //				if (lacksAlias(criteria, alias)) {
 //					criteria.createAlias(property, alias);
 //				}
 //
 //				List<Optional<Criterion>> criterionList = new ArrayList<>();
 //				criterionList.add(Optional.of(eq(String.format("%s.targetUuid", alias), param.getIdPart())));
 //				criterionList.add(Optional.of(eq(String.format("%s.type", alias), param.getResourceType())));
 //				return Optional.of(and(toCriteriaArray(criterionList)));
 //			}
 //
 //			return Optional.empty();
 //		}).ifPresent(criteria::add);
 //	}

 private Boolean validReferenceParam(ReferenceParam ref) {
  return (ref != null && ref.getIdPart() != null && ref.getResourceType() != null);
 }
}
