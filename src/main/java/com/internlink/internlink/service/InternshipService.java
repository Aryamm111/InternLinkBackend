package com.internlink.internlink.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
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
import com.mongodb.client.MongoCollection;
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
    @Autowired
    private InteractionService interactionService;

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

    public List<Internship> searchInternships(String title, String location, String studentId, int page, int size) {
        Query query = new Query();
        query.addCriteria(Criteria.where("status").is("active"));

        String studentMajor = studentService.getStudentMajor(studentId);
        if (studentMajor == null || studentMajor.isEmpty()) {
            return new ArrayList<>();
        }
        query.addCriteria(Criteria.where("majors").in(studentMajor));

        if (title != null && !title.isEmpty()) {
            query.addCriteria(Criteria.where("$text").is(new BasicDBObject("$search", title)));
        }

        if (location != null && !location.isEmpty()) {
            query.addCriteria(Criteria.where("location").is(location));
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        query.with(pageable);

        return mongoTemplate.find(query, Internship.class);
    }

    public List<Internship> recommendForStudent(String studentId) {
        // Get the student's profile embedding vector
        INDArray profileVector = studentService.getStudentProfileVector(studentId);
        // Retrieve internships the student has interacted with
        List<Internship> interacted = interactionService.findInteractedInternships(studentId);

        INDArray finalVector;
        if (interacted.isEmpty()) {
            finalVector = profileVector; // If no interactions, use only profile vector
        } else {
            // Average the embeddings of interacted internships
            INDArray combinedVector = Nd4j.zeros(384);
            for (Internship i : interacted) {
                combinedVector.addi(toINDArray(i.getEmbedding()));
            }
            combinedVector.divi(interacted.size());
            // Combine profile vector with interacted internship embeddings
            finalVector = profileVector.mul(0.7).add(combinedVector.mul(0.3));
        }
        // Convert final embedding to a list format
        List<Float> finalVectorList = toList(finalVector);

        // Get internship collection from MongoDB
        MongoCollection<Document> collection = mongoTemplate.getCollection("internships");

        // Build vector search query using MongoDB's indexing
        Document vectorSearchQuery = new Document("$vectorSearch",
                new Document("index", "internship_index2") // Use your actual index name
                        .append("queryVector", finalVectorList)
                        .append("path", "embedding")
                        .append("numCandidates", 300)
                        .append("k", 20)
                        .append("limit", 15));

        // Filter for active internships
        Document matchStage = new Document("$match", new Document("status", "active"));
        Document projectStage = new Document("$project", new Document("embedding", 0)); // Exclude embedding from
                                                                                        // results
        // Execute query and retrieve results
        List<Document> rawResults = collection
                .aggregate(List.of(vectorSearchQuery, matchStage, projectStage))
                .into(new ArrayList<>());
        // Convert raw MongoDB documents to Internship objects
        return rawResults.stream()
                .map(doc -> mongoTemplate.getConverter().read(Internship.class, doc))
                .toList();
    }

    private INDArray toINDArray(List<Float> vector) {
        float[] floatArray = new float[vector.size()];
        for (int i = 0; i < vector.size(); i++) {
            floatArray[i] = vector.get(i);
        }
        return Nd4j.create(floatArray);
    }

    private List<Float> toList(INDArray array) {
        float[] floats = array.toFloatVector();
        List<Float> list = new ArrayList<>(floats.length);
        for (float f : floats)
            list.add(f);
        return list;
    }

    public List<Internship> getUploadedInternships(String hrManagerId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("uploadedBy").is(hrManagerId));
        return mongoTemplate.find(query, Internship.class);
    }

    public Internship buildInternshipFromRequest(
            Internship existingInternship,
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

        // Generating embeddings for internship
        String combinedText = title + " " + description + " " +
                String.join(" ", internship.getRequiredSkills()) + " " +
                String.join(" ", internship.getMajors());
        String normalizedText = combinedText.trim().toLowerCase().replaceAll("\\s+", " ");
        List<Float> embedding = embeddingService.generateEmbedding(normalizedText);
        internship.setEmbedding(embedding);

        // Save internship plan if new file is uploaded
        if (planFile != null && !planFile.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + planFile.getOriginalFilename();
            Path filePath = Paths.get("uploads/plans/" + fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, planFile.getBytes());
            internship.setInternshipPlanUrl("http://localhost:8081/uploads/plans/" + fileName);
        } else if (existingInternship != null) {
            internship.setInternshipPlanUrl(existingInternship.getinternshipPlanUrl());
        }

        // Save image file if new file is uploaded
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path imagePath = Paths.get("uploads/images/" + imageName);
            Files.createDirectories(imagePath.getParent());
            Files.write(imagePath, imageFile.getBytes());
            internship.setImageUrl("http://localhost:8081/uploads/images/" + imageName);
        } else if (existingInternship != null) {
            internship.setImageUrl(existingInternship.getImageUrl());
        }

        return internship;
    }

    public boolean softDeleteInternship(String id) {
        Query query = new Query(where("_id").is(id));

        Update update = new Update().set("status", "deleted");

        UpdateResult result = mongoTemplate.updateFirst(query, update, Internship.class);

        return result.getModifiedCount() > 0;
    }

}