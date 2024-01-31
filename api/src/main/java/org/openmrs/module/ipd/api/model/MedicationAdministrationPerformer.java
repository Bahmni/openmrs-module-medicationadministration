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

import javax.persistence.*;

/**
 * The MedicationAdministration class records detailed information about the provision of a supply of a medication
 * with the intention that it is subsequently consumed by a patient (usually in response to a prescription).
 *
 * @see <a href="https://hl7.org/fhir/R4/medicationadministration-definitions.html#MedicationAdministration.performer">
 *     		https://hl7.org/fhir/R4/medicationadministration-definitions.html#MedicationAdministration.performer
 *     	</a>
 * @since 2.5.12
 */
@Entity
@Table(name = "medication_administration_performer")
public class MedicationAdministrationPerformer extends BaseFormRecordableOpenmrsData {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "medication_administration_performer_id")
	private Integer medicationAdministrationPerformerId;

	/**
	 * FHIR:actor
	 * Indicates who or what performed the medication administration and how they were involved.
	 */
	@OneToOne(optional = false)
	@JoinColumn(name = "actor_id")
	private Provider actor;

	/**
	 * FHIR:function
	 * @see <a href="https://hl7.org/fhir/R4/valueset-med-admin-perform-function.html">
	 *     		https://hl7.org/fhir/R4/valueset-med-admin-perform-function.html
	 *     	</a>
	 * i.e. performer, verifier, witness
	 */
	@ManyToOne(optional = true)
	@JoinColumn(name = "function")
	private Concept function;

	public MedicationAdministrationPerformer() {
	}

	/**
	 * @see BaseOpenmrsObject#getId()
	 */
	@Override
	public Integer getId() {
		return getMedicationAdministrationPerformerId();
	}

	/**
	 * @see BaseOpenmrsObject#setId(Integer)
	 */
	@Override
	public void setId(Integer id) {
		setMedicationAdministrationPerformerId(id);
	}

	public Integer getMedicationAdministrationPerformerId() {
		return medicationAdministrationPerformerId;
	}

	public void setMedicationAdministrationPerformerId(Integer medicationAdministrationPerformerId) {
		this.medicationAdministrationPerformerId = medicationAdministrationPerformerId;
	}

	public Provider getActor() {
		return actor;
	}

	public void setActor(Provider actor) {
		this.actor = actor;
	}

	public Concept getFunction() {
		return function;
	}

	public void setFunction(Concept function) {
		this.function = function;
	}

}
