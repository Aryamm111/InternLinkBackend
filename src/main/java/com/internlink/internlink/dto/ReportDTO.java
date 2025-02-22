package com.internlink.internlink.dto;

public class ReportDTO {
	private String id;
	private String studentName;
	private String submissionDate;
	private String status;

	public ReportDTO(String id, String studentName, String submissionDate, String status) {
		this.id = id;
		this.studentName = studentName;
		this.submissionDate = submissionDate;
		this.status = status;
	}

	// Getters and Setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(String submissionDate) {
		this.submissionDate = submissionDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
