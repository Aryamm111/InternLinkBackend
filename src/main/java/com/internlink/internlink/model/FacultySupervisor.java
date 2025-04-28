package com.internlink.internlink.model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "facultySupervisors") // Store FacultySupervisors in the 'facultysupervisors' collection
public class FacultySupervisor extends User {

    private String supervisorId;
    private List<String> studentIds; // List of student IDs supervised
    private List<String> reportIds; // List of report IDs submitted by students

    // Default constructor (required by Spring Boot)
    public FacultySupervisor() {
        super(); // Call the parent (User) constructor
    }

    // Constructor to initialize with specific fields
    public FacultySupervisor(String email, String password, String name, List<String> studentIds,
            List<String> reportIds) {
        super(email, password, name); // Pass to the User constructor
        this.studentIds = studentIds;
        this.reportIds = reportIds;
    }

    // Getters and Setters
    public List<String> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<String> studentIds) {
        this.studentIds = studentIds;
    }

    public List<String> getReportIds() {
        return reportIds;
    }

    public void setReportIds(List<String> reportIds) {
        this.reportIds = reportIds;
    }

    public String getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(String supervisorId) {
        this.supervisorId = supervisorId;
    }
}
