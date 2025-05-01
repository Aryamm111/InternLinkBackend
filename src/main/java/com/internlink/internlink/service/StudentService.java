package com.internlink.internlink.service;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.internlink.internlink.model.Student;

import ai.djl.translate.TranslateException;

@Service
public class StudentService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private EmbeddingService embeddingService;

    public Student getStudentById(String studentId) {
        return mongoTemplate.findById(studentId, Student.class);
    }

    public Map<String, String> getSupervisorIds(String studentId) {
        Query query = new Query(Criteria.where("_id").is(studentId));
        query.fields()
                .include("facultySupervisorId")
                .include("companySupervisorId");

        Document student = mongoTemplate.findOne(query, Document.class, "students");
        if (student == null)
            return null;

        return Map.of(
                "facultySupervisorId", student.getString("facultySupervisorId"),
                "companySupervisorId", student.getString("companySupervisorId"));
    }

    public String getStudentMajor(String studentId) {
        Query query = new Query(Criteria.where("_id").is(studentId));
        Student student = mongoTemplate.findOne(query, Student.class);
        if (student == null) {
            throw new RuntimeException("Student not found!" + studentId);
        }
        return student.getMajor();
    }

    public Student register(Student student) {
        if (userService.userExistsByEmail(student.getEmail())) {
            throw new IllegalArgumentException("Email already exists!");
        }
        return mongoTemplate.save(student);
    }

    public void assignSupervisorToStudents(String supervisorId, List<String> studentIds) {
        Query query = new Query(Criteria.where("_id").in(studentIds));
        Update update = new Update().set("companySupervisorId", supervisorId);
        mongoTemplate.updateMulti(query, update, Student.class);
    }

    public Student updateStudent(String studentId, Student updatedStudent) throws TranslateException {
        Student student = getStudentById(studentId);
        if (student == null) {
            return null;
        }

        if (updatedStudent.getSkills() != null) {
            student.setSkills(updatedStudent.getSkills());
        }
        if (updatedStudent.getLocation() != null) {
            student.setLocation(updatedStudent.getLocation());
        }

        // Regenerate embeddings
        String text = student.getMajor() + "   " + student.getLocation() + "  " + student.getSkills();
        List<Float> embedding = embeddingService.generateEmbedding(text);
        student.setEmbedding(embedding);

        return mongoTemplate.save(student);
    }

    public void deleteStudent(String studentId) {
        Student student = getStudentById(studentId);
        if (student != null) {
            mongoTemplate.remove(student);
        }
    }

    public boolean existsById(String studentId) {
        return mongoTemplate.exists(new Query(Criteria.where("_id").is(studentId)), Student.class);
    }

    public List<Float> getStudentEmbedding(String studentId) {

        Student student = mongoTemplate.findById(studentId, Student.class);
        if (student == null) {

            System.err.println("Student not found with ID: " + studentId);
            throw new IllegalStateException("Student embedding not found!");
        }

        List<Float> embedding = student.getEmbedding();
        if (embedding == null || embedding.isEmpty()) {

            System.err.println("Embedding not found or empty for student ID: " + studentId);
            throw new IllegalStateException("Student embedding not found!");
        }

        System.out.println("Embedding successfully fetched for student ID: " + studentId);
        return embedding;
    }

    public boolean assignCompanySupervisorToStudents(String supervisorId, List<String> studentIds) {
        if (studentIds == null || studentIds.isEmpty()) {
            return false;
        }

        Query query = new Query(Criteria.where("_id").in(studentIds)); // `_id` is now `studentId`
        Update update = new Update().set("companySupervisorId", supervisorId); // Set company supervisor

        // Update all matching students
        var result = mongoTemplate.updateMulti(query, update, Student.class);

        // Return true if any students were updated
        return result.getModifiedCount() > 0;
    }

    public Student assignFacultySupervisor(String studentId, String facultySupervisorId) {
        Query query = new Query(Criteria.where("_id").is(studentId));

        Student student = mongoTemplate.findOne(query, Student.class);
        if (student == null) {
            return null; // Student not found
        }
        Update update = new Update().set("facultySupervisorId", facultySupervisorId);
        mongoTemplate.updateFirst(query, update, Student.class);

        return mongoTemplate.findOne(query, Student.class);
    }

    public List<Student> getStudentsByFacultySupervisor(String facultySupervisorId) {
        Query query = new Query(Criteria.where("facultySupervisorId").is(facultySupervisorId));
        query.fields().exclude("embedding"); // Exclude the "embedding" field
        return mongoTemplate.find(query, Student.class);
    }

    public List<Student> getStudentsByCompanySupervisor(String companySupervisorId) {
        Query query = new Query(Criteria.where("companySupervisorId").is(companySupervisorId));
        query.fields().exclude("embedding"); // Exclude the "embedding" field to minimize the response size
        return mongoTemplate.find(query, Student.class);
    }

}
