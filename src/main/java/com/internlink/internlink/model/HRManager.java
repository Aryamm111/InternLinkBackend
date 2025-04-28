package com.internlink.internlink.model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "hrmanagers")
public class HRManager extends User {

    private String companyName;
    private List<String> supervisorIds;

    public HRManager() {
        super();
    }

    public HRManager(String email, String password, String name) {
        super(email, password, name);
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<String> getSupervisorIds() {
        return supervisorIds;
    }

    public void setSupervisorIds(List<String> supervisorIds) {
        this.supervisorIds = supervisorIds;
    }

}
