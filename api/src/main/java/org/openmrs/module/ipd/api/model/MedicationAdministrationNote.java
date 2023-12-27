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
import java.util.Date;
import java.util.Objects;

/**
 * The MedicationAdministrationNote class records a text note which also contains information about who made the statement and when.
 *
 * @see <a href="https://hl7.org/fhir/R4/datatypes.html#Annotation">
 *     		https://hl7.org/fhir/R4/datatypes.html#Annotation
 *     	</a>
 * @since 2.5.12
 */
@Entity
@Table(name = "medication_administration_note")
public class MedicationAdministrationNote extends BaseOpenmrsData {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "medication_administration_note_id")
	private Integer medicationAdministrationNoteId;

	/**
	 * FHIR:author
	 * Who made the statement
	 */
	@OneToOne(optional = true)
	@JoinColumn(name = "author_id")
	private Provider author;

	/**
	 * FHIR:time
	 * Time when the statement was recorded
	 */
	@Column(name = "recorded_time")
	private Date recordedTime;

	/**
	 * FHIR:text
	 * The statement - a text note associated with the annotation.
	 */
	@Column(name = "text", length=65535)
	private String text;

	public MedicationAdministrationNote() {
	}

	/**
	 * @see BaseOpenmrsObject#getId()
	 */
	@Override
	public Integer getId() {
		return getMedicationAdministrationNoteId();
	}

	/**
	 * @see BaseOpenmrsObject#setId(Integer)
	 */
	@Override
	public void setId(Integer id) {
		setMedicationAdministrationNoteId(id);
	}

	public Integer getMedicationAdministrationNoteId() {
		return medicationAdministrationNoteId;
	}

	public void setMedicationAdministrationNoteId(Integer medicationAdministrationNoteId) {
		this.medicationAdministrationNoteId = medicationAdministrationNoteId;
	}

	public Provider getAuthor() {
		return author;
	}

	public void setAuthor(Provider author) {
		this.author = author;
	}

	public Date getRecordedTime() {
		return recordedTime;
	}

	public void setRecordedTime(Date recordedTime) {
		this.recordedTime = recordedTime;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;

		MedicationAdministrationNote other = (MedicationAdministrationNote) obj;
		boolean yes= Objects.equals(this.medicationAdministrationNoteId, other.medicationAdministrationNoteId)
				|| Objects.equals(this.getUuid(),other.getUuid());
		return yes;
	}

	@Override
	public int hashCode() {
		int hash = Objects.hash(this.getUuid());
		return hash;
	}

}
