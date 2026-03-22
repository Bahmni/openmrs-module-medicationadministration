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

/**
 * The MedicationAdministrationNote class records a text note which also contains information about who made the statement and when.
 *
 * @see <a href="https://hl7.org/fhir/R4/datatypes.html#Annotation">
 *     		https://hl7.org/fhir/R4/datatypes.html#Annotation
 *     	</a>
 * @since 2.5.12
 */
public class MedicationAdministrationNote extends BaseOpenmrsData {

	private static final long serialVersionUID = 1L;

	private Integer medicationAdministrationNoteId;

	private Provider author;

	private Date recordedTime;

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

}
