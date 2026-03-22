/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.ipd.api.model;

import org.openmrs.*;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * The MedicationAdministration class records detailed information about the provision of a supply of a medication
 * with the intention that it is subsequently consumed by a patient (usually in response to a prescription).
 *
 * @see <a href="https://www.hl7.org/fhir/medicationadministration.html">
 *     		https://www.hl7.org/fhir/medicationadministration.html
 *     	</a>
 * @since 2.5.12
 */
public class MedicationAdministration extends BaseFormRecordableOpenmrsData {

	private static final long serialVersionUID = 1L;

	private Integer medicationAdministrationId;

	private Patient patient;

	private Encounter encounter;

	private Drug drug;

	private Set<MedicationAdministrationPerformer> performers;

	private DrugOrder drugOrder;

	private org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationStatus status;

	private Concept statusReason;

	private Date administeredDateTime;

	private String dosingInstructions;

	private Double dose;

	private Concept doseUnits;

	private Concept route;

	private Concept site;

	private Set<MedicationAdministrationNote> notes;


	public MedicationAdministration() {
	}

	/**
	 * @see BaseOpenmrsObject#getId()
	 */
	@Override
	public Integer getId() {
		return getMedicationAdministrationId();
	}

	/**
	 * @see BaseOpenmrsObject#setId(Integer)
	 */
	@Override
	public void setId(Integer id) {
		setMedicationAdministrationId(id);
	}

	public Integer getMedicationAdministrationId() {
		return medicationAdministrationId;
	}

	public void setMedicationAdministrationId(Integer medicationAdministrationId) {
		this.medicationAdministrationId = medicationAdministrationId;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Encounter getEncounter() {
		return encounter;
	}

	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}

	public Drug getDrug() {
		return drug;
	}

	public void setDrug(Drug drug) {
		this.drug = drug;
	}

	public Set<MedicationAdministrationPerformer> getPerformers() {
		return performers;
	}

	public void setPerformers(Set<MedicationAdministrationPerformer> performers) {
		this.performers = performers;
	}

	public DrugOrder getDrugOrder() {
		return drugOrder;
	}

	public void setDrugOrder(DrugOrder drugOrder) {
		this.drugOrder = drugOrder;
	}

	public org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationStatus getStatus() {
		return status;
	}

	public void setStatus(org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationStatus status) {
		this.status = status;
	}

	public Concept getStatusReason() {
		return statusReason;
	}

	public void setStatusReason(Concept statusReason) {
		this.statusReason = statusReason;
	}

	public Date getAdministeredDateTime() {
		return administeredDateTime;
	}

	public void setAdministeredDateTime(Date administeredDateTime) {
		this.administeredDateTime = administeredDateTime;
	}

	public Double getDose() {
		return dose;
	}

	public void setDose(Double dose) {
		this.dose = dose;
	}

	public String getDosingInstructions() {
		return dosingInstructions;
	}

	public void setDosingInstructions(String dosingInstructions) {
		this.dosingInstructions = dosingInstructions;
	}

	public Concept getDoseUnits() {
		return doseUnits;
	}

	public void setDoseUnits(Concept doseUnits) {
		this.doseUnits = doseUnits;
	}

	public Concept getRoute() {
		return route;
	}

	public void setRoute(Concept route) {
		this.route = route;
	}

	public Concept getSite() {
		return site;
	}

	public void setSite(Concept site) {
		this.site = site;
	}

	public Set<MedicationAdministrationNote> getNotes() {
		return notes;
	}

	public void setNotes(Set<MedicationAdministrationNote> notes) {
		this.notes = notes;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;

		MedicationAdministration other = (MedicationAdministration) obj;
		boolean yes= Objects.equals(this.medicationAdministrationId, other.medicationAdministrationId)
				|| Objects.equals(this.getUuid(),other.getUuid());
		return yes;
	}

	@Override
	public int hashCode() {
		int hash = Objects.hash(this.getUuid());
		return hash;
	}

}
