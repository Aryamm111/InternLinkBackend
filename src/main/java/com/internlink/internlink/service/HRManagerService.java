package com.internlink.internlink.service;
// NEW HR MANAGER SERVICE
import com.internlink.internlink.model.Application;
import com.internlink.internlink.model.HRManager;
import com.internlink.internlink.model.Internship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HRManagerService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // Create an HRManager account
    public HRManager createAccount(HRManager hrManager) {
        return mongoTemplate.save(hrManager);
    }

     // View applications associated with an HRManager
     public List<Application> viewApplications(String hrManagerId) {
        Query query = new Query(Criteria.where("HRmanagerId").is(hrManagerId));
        HRManager hrManager = mongoTemplate.findOne(query, HRManager.class);

        if (hrManager == null) {
            throw new RuntimeException("HR Manager not found with ID: " + hrManagerId);
        }

        return hrManager.getApplications();
    }

    // Announce a new internship opportunity
    public Internship announceInternship(String hrManagerId, Internship internshipOpportunity) {
        Query query = new Query(Criteria.where("HRmanagerId").is(hrManagerId));
        HRManager hrManager = mongoTemplate.findOne(query, HRManager.class);

        if (hrManager == null) {
            throw new RuntimeException("HR Manager not found with ID: " + hrManagerId);
        }

        // Save the internship opportunity
        mongoTemplate.save(internshipOpportunity);

        // Add the internship opportunity to the HRManager's list
        Update update = new Update().push("internshipOpportunities", internshipOpportunity);
        mongoTemplate.updateFirst(query, update, HRManager.class);

        return internshipOpportunity;
    }


} // end class 
