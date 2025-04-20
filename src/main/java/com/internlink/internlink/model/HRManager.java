package com.internlink.internlink.model;
//New HR Model 
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import java.util.List;

@Document(collection = "HRmanager")
public class HRManager extends User {

    @Indexed(unique = true)
    private String HRmanagerId;
    private String companyName;

 @DBRef
    private List<Application> applications; // 1 to 1..* relationship with Application

 @DBRef
    private List<Internship> internshipOpportunities; // 1 to 1..* relationship with InternshipOpportunity


    
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

     public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    public List<Internship> getInternshipOpportunities() {
        return internshipOpportunities;
    }

    public void setInternshipOpportunities(List<Internship> internshipOpportunities) {
        this.internshipOpportunities = internshipOpportunities;
    }

} // END HR model
