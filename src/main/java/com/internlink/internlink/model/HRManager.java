package com.internlink.internlink.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "HRmanager")
public class HRManager extends User {

    @Indexed(unique = true)
    private String HRmanagerId;
    public String companyName;

    public String getHRmanagerId() {
        return HRmanagerId;
    }

    public void setHRmanagerId(String HRmanagerId) {
        this.HRmanagerId = HRmanagerId;
    }

    public String getcompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

}