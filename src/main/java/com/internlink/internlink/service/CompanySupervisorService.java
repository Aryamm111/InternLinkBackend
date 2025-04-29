package com.internlink.internlink.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.internlink.internlink.model.CompanySupervisor;
import com.internlink.internlink.model.HRManager;

@Service
public class CompanySupervisorService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;

    public ResponseEntity<?> registerCompanySupervisor(CompanySupervisor companySupervisor) {
        if (userService.userExistsByEmail(companySupervisor.getEmail())) {
            throw new IllegalArgumentException("Email already exists!");
        }
        Query query = new Query();
        String hrManagerId = companySupervisor.getHrManagerId();
        query.addCriteria(Criteria.where("_id").is(hrManagerId));
        HRManager hrManager = mongoTemplate.findOne(query, HRManager.class, "hrmanagers");

        if (hrManager == null || !hrManager.getCompanyName().equalsIgnoreCase(companySupervisor.getCompanyName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid HR Manager ID or company name does not match.");
        }

        companySupervisor.setPassword(passwordEncoder.encode(companySupervisor.getPassword()));
        companySupervisor.setUserRole("COMPANY_SUPERVISOR");
        companySupervisor.setHrManagerId(hrManagerId);
        mongoTemplate.save(companySupervisor, "companySupervisors");

        if (hrManager.getSupervisorIds() == null) {
            hrManager.setSupervisorIds(new ArrayList<>());
        }
        hrManager.getSupervisorIds().add(companySupervisor.getId());

        mongoTemplate.save(hrManager, "hrmanagers");

        return ResponseEntity.ok("Company Supervisor registered and linked to HR Manager.");
    }

    public List<CompanySupervisor> getByHRManagerId(String hrManagerId) {
        Query query = new Query(Criteria.where("hrManagerId").is(hrManagerId));
        return mongoTemplate.find(query, CompanySupervisor.class);
    }

}
