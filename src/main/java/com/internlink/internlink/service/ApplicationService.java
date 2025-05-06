package com.internlink.internlink.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.internlink.internlink.model.Application;
import com.internlink.internlink.model.Internship;
import com.internlink.internlink.model.Student;

@Service
public class ApplicationService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void saveApplication(
            String internshipId,
            String studentId,
            String internshipTitle,
            MultipartFile applicationLetter,
            MultipartFile academicRecord,
            MultipartFile cv,
            String skills) throws IOException {

        Application application = new Application();
        application.setInternshipId(internshipId);
        application.setStudentId(studentId);
        application.setInternshipTitle(internshipTitle);
        application.setSkills(skills);

        // Handle application letter upload
        if (applicationLetter != null && !applicationLetter.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + applicationLetter.getOriginalFilename();
            Path filePath = Paths.get("uploads/application-letters/" + fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, applicationLetter.getBytes());
            application.setLetter("http://localhost:8081/uploads/application-letters/" + fileName);
        }

        // Handle academic record upload
        if (academicRecord != null && !academicRecord.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + academicRecord.getOriginalFilename();
            Path filePath = Paths.get("uploads/academic-records/" + fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, academicRecord.getBytes());
            application.setAcademicRecord("http://localhost:8081/uploads/academic-records/" + fileName);
        }

        // Handle CV upload
        if (cv != null && !cv.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + cv.getOriginalFilename();
            Path filePath = Paths.get("uploads/cvs/" + fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, cv.getBytes());
            application.setCv("http://localhost:8081/uploads/cvs/" + fileName);
        }

        application.setAppliedOn(LocalDateTime.now());
        application.setStatus("pending");

        mongoTemplate.save(application, "applications");
    }

    public List<Application> getApplicationsForInternship(String internshipId) {
        Query query = new Query(Criteria.where("internshipId").is(internshipId));
        return mongoTemplate.find(query, Application.class);
    }

    public List<Application> viewApplication(String studentId) { // previously find application by student id
        Query query = new Query();
        query.addCriteria(Criteria.where("studentId").is(studentId));
        return mongoTemplate.find(query, Application.class, "applications");
    }

    // public List<Application> findApplicationsByInternshipId(String internshipId)
    // {
    // Query query = new Query();
    // query.addCriteria(Criteria.where("internshipId").is(internshipId));
    // return mongoTemplate.find(query, Application.class, "applications");
    // }

    public void updateStatus(String applicationId, String status) {
        if (!status.equals("Accepted") && !status.equals("Rejected") && !status.equals("Pending")) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }

        Query appQuery = new Query(Criteria.where("_id").is(applicationId));
        Update update = new Update().set("status", status);
        mongoTemplate.updateFirst(appQuery, update, Application.class);

        if ("Accepted".equalsIgnoreCase(status)) {
            Application application = getApplicationById(applicationId);
            if (application != null) {
                String internshipId = application.getInternshipId();
                String studentId = application.getStudentId();

                // Use addToSet to ensure student is added only once
                Query internshipQuery = new Query(Criteria.where("_id").is(internshipId));
                Update internshipUpdate = new Update().addToSet("students", studentId);
                mongoTemplate.updateFirst(internshipQuery, internshipUpdate, Internship.class);
            }
        }
    }

    public List<Student> getAcceptedStudentsForHr(String hrManagerId) {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.addFields()
                        .addFieldWithValue("internshipIdObj",
                                ConvertOperators.ToObjectId.toObjectId("$internshipId"))
                        .build(),

                Aggregation.lookup("internships", "internshipIdObj", "_id", "internshipData"),
                Aggregation.unwind("internshipData"),
                Aggregation.match(
                        Criteria.where("internshipData.uploadedBy").is(hrManagerId)
                                .and("status").is("Accepted")),
                Aggregation.project("studentId"));

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "applications", Document.class);
        List<String> studentIds = results.getMappedResults().stream()
                .map(doc -> doc.getString("studentId"))
                .collect(Collectors.toList());

        if (studentIds.isEmpty()) {
            return new ArrayList<>();
        }

        Query query = new Query(Criteria.where("_id").in(studentIds));
        return mongoTemplate.find(query, Student.class);
    }

    public Application getApplicationById(String applicationId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(applicationId));
        return mongoTemplate.findOne(query, Application.class, "applications");
    }

}
