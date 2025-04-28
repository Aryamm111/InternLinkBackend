package com.internlink.internlink.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "companySupervisors")
public class CompanySupervisor extends User {

    private String hrManagerId;
    private String companyName;

    public CompanySupervisor() {
        super();
    }

    public String getHrManagerId() {
        return hrManagerId;
    }

    public void setHrManagerId(String hrManagerId) {
        this.hrManagerId = hrManagerId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
