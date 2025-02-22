package com.internlink.internlink.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "CompanySupervisor")
public class CompanySupervisor extends User {

    @Indexed(unique = true)
    private String companySupervisorId;
    public String companyName;
    public String department;

    public String getCompanySupervisorId() {
        return companySupervisorId;
    }

    public void setCompanySupervisorId(String companySupervisorId) {
        this.companySupervisorId = companySupervisorId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

}