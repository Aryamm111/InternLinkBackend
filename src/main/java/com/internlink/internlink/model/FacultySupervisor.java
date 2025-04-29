package com.internlink.internlink.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "facultySupervisors")
public class FacultySupervisor extends User {

    private String supervisorId;

    public FacultySupervisor() {
        super();
    }

    // Constructor to initialize with specific fields
    public FacultySupervisor(String email, String password, String name) {
        super(email, password, name); // Pass to the User constructor

    }

    public String getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(String supervisorId) {
        this.supervisorId = supervisorId;
    }
}
