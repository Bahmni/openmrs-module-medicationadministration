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

	/**
	 * Amended text - the updated text when note is amended
	 */
	@Column(name = "amended_text", length=65535, nullable = true)
	private String amendedText;

	/**
	 * Amendment reason - why this note was amended
	 */
	@Column(name = "amended_reason", nullable = true)
	private String amendedReason;

	/**
	 * Provider who approved this amendment
	 */
	@OneToOne(optional = true)
	@JoinColumn(name = "approved_by_id", nullable = true)
	private Provider approvedBy;

	/**
	 * Date and time when the amendment was approved
	 */
	@Column(name = "approved_date_time", nullable = true)
	private Date approvedDateTime;

	/**
	 * Status of the approval (PENDING, APPROVED, REJECTED)
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "approval_status", nullable = true)
	private ApprovalStatus approvalStatus;

	/**
	 * Notes related to the approval decision
	 */
	@Column(name = "approval_notes", length=65535, nullable = true)
	private String approvalNotes;

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

	public String getAmendedText() {
		return amendedText;
	}

	public void setAmendedText(String amendedText) {
		this.amendedText = amendedText;
	}

	public String getAmendedReason() {
		return amendedReason;
	}

	public void setAmendedReason(String amendedReason) {
		this.amendedReason = amendedReason;
	}

	public Provider getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(Provider approvedBy) {
		this.approvedBy = approvedBy;
	}

	public Date getApprovedDateTime() {
		return approvedDateTime;
	}

	public void setApprovedDateTime(Date approvedDateTime) {
		this.approvedDateTime = approvedDateTime;
	}

	public ApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public String getApprovalNotes() {
		return approvalNotes;
	}

	public void setApprovalNotes(String approvalNotes) {
		this.approvalNotes = approvalNotes;
	}

}
