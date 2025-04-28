package com.internlink.internlink.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "report")
public class Report {

    @Id
    private String id;
    private String studentId;
    private String facultySupervisorId;
    private String companySupervisorId;
    private String fileUrl;
    private boolean isVerified;
    private String studentName;

    private LocalDateTime submissionDate;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getFacultySupervisorId() {
        return facultySupervisorId;
    }

    public void setFacultySupervisorId(String facultySupervisorId) {
        this.facultySupervisorId = facultySupervisorId;
    }

    public String getCompanySupervisorId() {
        return companySupervisorId;
    }

    public void setCompanySupervisorId(String companySupervisorId) {
        this.companySupervisorId = companySupervisorId;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
