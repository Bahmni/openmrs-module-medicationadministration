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

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Provider;

import javax.persistence.*;
import java.util.Date;

/**
 * The MedicationAdministrationAcknowledgement class records when a medication administration
 * record has been reviewed and acknowledged by a supervisor/verifier, which locks the record
 * from further amendments.
 *
 * Maps to FHIR R4 Provenance resource with agent.type = "verifier"
 *
 * @see <a href="https://hl7.org/fhir/R4/provenance.html">
 *     		https://hl7.org/fhir/R4/provenance.html
 *     	</a>
 * @since 2.5.12
 */
@Entity
@Table(name = "medication_administration_acknowledgement")
public class MedicationAdministrationAcknowledgement extends BaseOpenmrsData {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "acknowledgement_id")
	private Integer acknowledgementId;

	/**
	 * The medication administration record being acknowledged
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "medication_administration_id", nullable = false)
	private MedicationAdministration medicationAdministration;

	/**
	 * The provider (supervisor/verifier) who acknowledged the record
	 * FHIR:Provenance.agent.who
	 */
	@ManyToOne
	@JoinColumn(name = "provider_id", nullable = false)
	private Provider provider;

	/**
	 * Type of acknowledgement action (e.g., "ACKNOWLEDGE")
	 * FHIR:Provenance.agent.type
	 */
	@Column(name = "action_type", nullable = false, length = 50)
	private String actionType;

	/**
	 * When the acknowledgement was recorded
	 * FHIR:Provenance.recorded
	 */
	@Column(name = "action_datetime", nullable = false)
	private Date actionDatetime;

	/**
	 * Optional remarks/comments from the acknowledger
	 */
	@Column(name = "remarks", length = 65535)
	private String remarks;

	public MedicationAdministrationAcknowledgement() {
	}

	/**
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	@Override
	public Integer getId() {
		return getAcknowledgementId();
	}

	/**
	 * @see org.openmrs.OpenmrsObject#setId(Integer)
	 */
	@Override
	public void setId(Integer id) {
		setAcknowledgementId(id);
	}

	public Integer getAcknowledgementId() {
		return acknowledgementId;
	}

	public void setAcknowledgementId(Integer acknowledgementId) {
		this.acknowledgementId = acknowledgementId;
	}

	public MedicationAdministration getMedicationAdministration() {
		return medicationAdministration;
	}

	public void setMedicationAdministration(MedicationAdministration medicationAdministration) {
		this.medicationAdministration = medicationAdministration;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public Date getActionDatetime() {
		return actionDatetime;
	}

	public void setActionDatetime(Date actionDatetime) {
		this.actionDatetime = actionDatetime;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
