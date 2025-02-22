package com.internlink.internlink.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "FacultySupervisor")
public class FacultySupervisor extends User {

    @Indexed(unique = true)
    private String facultySupervisorId;
    public String major;

    public String getfacultySupervisorId() {
        return facultySupervisorId;
    }

    public void setFacultySupervisorId(String facultySupervisorId) {
        this.facultySupervisorId = facultySupervisorId;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

}