package com.internlink.internlink.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
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

        // Save the application to the database
        mongoTemplate.save(application, "applications");
    }

    public List<Application> getApplicationsForInternship(String internshipId) {
        Query query = new Query(Criteria.where("internshipId").is(internshipId));
        return mongoTemplate.find(query, Application.class);
    }

    public List<Application> findApplicationsByStudentId(String studentId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("studentId").is(studentId));
        return mongoTemplate.find(query, Application.class, "applications");
    }

    public List<Application> findApplicationsByInternshipId(String internshipId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("internshipId").is(internshipId));
        return mongoTemplate.find(query, Application.class, "applications");
    }

    public void updateStatus(String applicationId, String status) {
        // Validate that the status is one of the allowed values
        if (!status.equals("Accepted") && !status.equals("Rejected") && !status.equals("Pending")) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }

        // Update application status
        Query appQuery = new Query(Criteria.where("_id").is(applicationId));
        Update update = new Update().set("status", status);
        mongoTemplate.updateFirst(appQuery, update, Application.class);

        // If accepted, also add the student to the internship's list of students
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

    public List<Student> getAcceptedStudents() {
        // Step 1: Get applications with status = "Accepted"
        Query acceptedQuery = new Query(Criteria.where("status").is("Accepted"));
        List<Application> acceptedApps = mongoTemplate.find(acceptedQuery, Application.class, "applications");

        // Step 2: Extract unique studentIds
        List<String> studentIds = acceptedApps.stream()
                .map(Application::getStudentId)
                .distinct()
                .toList();

        // Step 3: Find full student objects
        Query studentQuery = new Query(Criteria.where("_id").in(studentIds));
        return mongoTemplate.find(studentQuery, Student.class, "students");
    }

    public Application getApplicationById(String applicationId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(applicationId)); // Corrected to `_id`
        return mongoTemplate.findOne(query, Application.class, "applications");
    }

}
