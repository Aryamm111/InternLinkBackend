package com.internlink.internlink.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "Reports")
public class Report {

    @Id
    private int reportID;
    private String studentName;
    private Date dueDate;
    private String file; // Path or URL to the uploaded file (MongoDB doesn't support java.io.File directly)
    private boolean isVerified;

    // Constructors
    public Report() {}

    public Report(int reportID, String studentName, Date dueDate, String file, boolean isVerified) {
        this.reportID = reportID;
        this.studentName = studentName;
        this.dueDate = dueDate;
        this.file = file;
        this.isVerified = isVerified;
    }


    public int getReportID() {
        return reportID;
    }

    public void setReportID(int reportID) {
        this.reportID = reportID;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getFilePath() {
        return file;
    }

    public void setVerified() {
        this.isVerified = true;
    }
}
