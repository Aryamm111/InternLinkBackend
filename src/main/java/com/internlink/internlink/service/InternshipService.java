// package com.internlink.internlink.service;

// import java.util.ArrayList;
// import java.util.List;

// import org.bson.Document;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.mongodb.core.MongoTemplate;
// import org.springframework.data.mongodb.core.query.Criteria;
// import org.springframework.data.mongodb.core.query.Query;
// import org.springframework.stereotype.Service;

// import com.internlink.internlink.model.Internship;

// @Service
// public class InternshipService {

// @Autowired
// private MongoTemplate mongoTemplate;

// public void createInternship(Internship internship) {
// mongoTemplate.save(internship);
// }

// public Internship getInternshipById(String internshipId) {
// return mongoTemplate.findById(internshipId, Internship.class);
// }

// public List<Internship> searchInternships(String title, String major, int
// page, int size) {
// Query query = new Query();

// if (title != null && !title.isEmpty()) {
// query.addCriteria(Criteria.where("title").regex(title, "i"));
// }

// if (major != null && !major.isEmpty()) {
// query.addCriteria(Criteria.where("majors").in(major));
// }

// Pageable pageable = PageRequest.of(page - 1, size);
// query.with(pageable);

// return mongoTemplate.find(query, Internship.class);
// }

// public List<Internship> getRecommendedInternships(List<Float>
// studentEmbedding) {
// System.out.println(" student embedding: " + studentEmbedding);

// if (studentEmbedding == null || studentEmbedding.isEmpty()) {
// System.err.println("Invalid student embedding: " + studentEmbedding);
// throw new IllegalArgumentException("Invalid student embedding!");
// }

// Document vectorSearchQuery = new Document("$vectorSearch",
// new Document("index", "internship_index2")
// .append("queryVector", studentEmbedding)
// .append("path", "embedding")
// .append("numCandidates", 15)
// .append("k", 15)
// .append("limit", 12));

// Document projectStage = new Document("$project",
// new Document("embedding", 0));

// System.out.println("Vector search query: " + vectorSearchQuery.toJson());

// List<Document> results = mongoTemplate.getCollection("internships")
// .aggregate(List.of(vectorSearchQuery, projectStage))
// .into(new ArrayList<>());
// List<Internship> internships = results.stream()
// .map(doc -> mongoTemplate.getConverter().read(Internship.class, doc))
// .toList();
// System.out.println("Number of internships found: " + internships.size());
// return internships;
// }

// public List<Internship> getUploadedInternships(String hrManagerId) {
// Query query = new Query();
// query.addCriteria(Criteria.where("uploadedBy").is(hrManagerId));
// return mongoTemplate.find(query, Internship.class);
// }

// }