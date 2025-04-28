package com.internlink.internlink.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internlink.internlink.model.Internship;
import com.mongodb.BasicDBObject;
import com.mongodb.client.result.UpdateResult;

import ai.djl.translate.TranslateException;

@Service
public class InternshipService {

    @Autowired
    private EmbeddingService embeddingService;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private StudentService studentService;

    public void createInternship(Internship internship) {
        mongoTemplate.save(internship);
    }

    public void saveInternship(Internship internship) {
        mongoTemplate.save(internship);
    }

    public Internship getInternshipById(String internshipId) {
        return mongoTemplate.findById(internshipId, Internship.class);
    }

    public void updateInternship(String internshipId, Internship updatedInternship) {
        Internship existing = getInternshipById(internshipId);
        if (existing == null) {
            throw new RuntimeException("Internship not found!");
        }

        updatedInternship.setId(internshipId); // Important: keep the same ID
        mongoTemplate.save(updatedInternship); // This acts as an update
    }

    public List<Internship> searchInternships(String title, String studentId, int page, int size) {
        Query query = new Query();

        // Always include only active internships
        query.addCriteria(Criteria.where("status").is("active"));

        // Fetch the student's major from the UserService
        String studentMajor = studentService.getStudentMajor(studentId); // Assuming you have this method

        if (studentMajor == null || studentMajor.isEmpty()) {
            // If the student does not have a major or it could not be fetched, return an
            // empty list
            return new ArrayList<>();
        }

        // Filter internships by the student's major
        query.addCriteria(Criteria.where("majors").in(studentMajor));

        // Include title filter if provided
        if (title != null && !title.isEmpty()) {
            query.addCriteria(Criteria.where("$text").is(new BasicDBObject("$search", title)));
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        query.with(pageable);

        return mongoTemplate.find(query, Internship.class);
    }

    public List<Internship> getRecommendedInternships(List<Float> studentEmbedding, String studentMajor) {
        System.out.println(" student embedding: " + studentEmbedding);

        if (studentEmbedding == null || studentEmbedding.isEmpty()) {
            System.err.println("Invalid student embedding: " + studentEmbedding);
            throw new IllegalArgumentException("Invalid student embedding!");
        }

        Document vectorSearchQuery = new Document("$vectorSearch",
                new Document("index", "internship_index2")
                        .append("queryVector", studentEmbedding)
                        .append("path", "embedding")
                        .append("numCandidates", 300)
                        .append("k", 15)
                        .append("limit", 12));

        Document matchStage = new Document("$match", new Document("status", "active")
                .append("majors", studentMajor)); // <<< here we filter by major

        Document projectStage = new Document("$project",
                new Document("embedding", 0));

        System.out.println("Vector search query: " + vectorSearchQuery.toJson());

        List<Document> results = mongoTemplate.getCollection("internships")
                .aggregate(List.of(vectorSearchQuery, matchStage, projectStage))
                .into(new ArrayList<>());

        List<Internship> internships = results.stream()
                .map(doc -> mongoTemplate.getConverter().read(Internship.class, doc))
                .toList();

        System.out.println("Number of internships found: " + internships.size());
        return internships;
    }

    public List<Internship> getUploadedInternships(String hrManagerId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("uploadedBy").is(hrManagerId));
        return mongoTemplate.find(query, Internship.class);
    }

    public Internship buildInternshipFromRequest(
            Internship existingInternship, // <- pass existing object
            String managerId,
            String title,
            String company,
            String location,
            String description,
            String startDateStr,
            String durationStr,
            String majorsJson,
            String skillsJson,
            String maxStudentsStr,
            MultipartFile planFile,
            MultipartFile imageFile,
            boolean isNew) throws IOException, TranslateException {

        Internship internship = new Internship();
        internship.setUploadedBy(managerId);
        internship.setTitle(title);
        internship.setCompany(company);
        internship.setLocation(location);
        internship.setDescription(description);
        internship.setStartDate(LocalDate.parse(startDateStr));
        internship.setDuration(Integer.parseInt(durationStr));
        internship.setMaxStudents(Integer.parseInt(maxStudentsStr));

        ObjectMapper mapper = new ObjectMapper();
        internship.setMajors(mapper.readValue(majorsJson, new TypeReference<List<String>>() {
        }));
        internship.setRequiredSkills(mapper.readValue(skillsJson, new TypeReference<List<String>>() {
        }));

        internship.setStudents(new ArrayList<>());

        String combinedText = title + " " + description + " " +
                String.join(" ", internship.getRequiredSkills()) + " " +
                String.join(" ", internship.getMajors());
        List<Float> embedding = embeddingService.generateEmbedding(combinedText);
        internship.setEmbedding(embedding);

        // Save internship plan if new file is uploaded
        if (planFile != null && !planFile.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + planFile.getOriginalFilename();
            Path filePath = Paths.get("uploads/plans/" + fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, planFile.getBytes());
            internship.setInternshipPlanUrl("http://localhost:8081/uploads/plans/" + fileName);
        } else if (existingInternship != null) {
            internship.setInternshipPlanUrl(existingInternship.getinternshipPlanUrl()); // <-- keep old URL
        }

        // Save image file if new file is uploaded
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path imagePath = Paths.get("uploads/images/" + imageName);
            Files.createDirectories(imagePath.getParent());
            Files.write(imagePath, imageFile.getBytes());
            internship.setImageUrl("http://localhost:8081/uploads/images/" + imageName);
        } else if (existingInternship != null) {
            internship.setImageUrl(existingInternship.getImageUrl()); // <-- keep old URL
        }

        return internship;
    }

    public boolean softDeleteInternship(String id) {
        // Define the query to find the document by its ID
        Query query = new Query(where("_id").is(id));

        // Define the update operation to set the status field to "deleted"
        Update update = new Update().set("status", "deleted");

        // Execute the update
        UpdateResult result = mongoTemplate.updateFirst(query, update, Internship.class);

        // Return true if a document was updated, false otherwise
        return result.getModifiedCount() > 0;
    }

}