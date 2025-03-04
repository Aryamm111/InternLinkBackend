package com.internlink.internlink.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.internlink.internlink.model.Student;

@Service
public class StudentService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // public List<Student> getAllStudents() {
    // return mongoTemplate.findAll(Student.class);
    // }

    public Student getStudentById(String studentId) {
        Query query = new Query(Criteria.where("studentId").is(studentId));
        return mongoTemplate.findOne(query, Student.class);
    }

    public Student register(Student student) {
        return mongoTemplate.save(student);
    }

    public Student updateStudent(String studentId, Student updatedStudent) {
        Student student = getStudentById(studentId);
        if (student == null)
            return null;

        student.setName(updatedStudent.getName());
        student.setEmail(updatedStudent.getEmail());
        student.setMajor(updatedStudent.getMajor());
        student.setUniversity(updatedStudent.getUniversity());
        return mongoTemplate.save(student);
    }

    public void deleteStudent(String studentId) {
        Student student = getStudentById(studentId);
        if (student != null) {
            mongoTemplate.remove(student);
        }
    }

    public String getStudentNameById(String studentId) {
        Student student = getStudentById(studentId);
        if (student == null) {
            throw new RuntimeException("Student not found with ID: " + studentId);
        }
        return student.getName();
    }

    public List<Student> getStudentsByFacultySupervisor(String facultySupervisorId) {
        return mongoTemplate.find(new Query(Criteria.where("facultySupervisorId").is(facultySupervisorId)),
                Student.class);
    }

    public List<Student> getStudentsByCompanySupervisor(String companySupervisorId) {
        return mongoTemplate.find(new Query(Criteria.where("companySupervisorId").is(companySupervisorId)),
                Student.class);
    }

    public Student assignFacultySupervisor(String studentId, String facultySupervisorId) {
        Query query = new Query(Criteria.where("studentId").is(studentId));
        Student student = mongoTemplate.findOne(query, Student.class);

        if (student == null) {
            return null;
        }

        Update update = new Update().set("facultySupervisorId", facultySupervisorId);
        mongoTemplate.updateFirst(query, update, Student.class);

        return mongoTemplate.findOne(query, Student.class);
    }

    public boolean assignCompanySupervisorToStudents(String supervisorId, List<String> studentIds) {
        if (studentIds == null || studentIds.isEmpty()) {
            return false;
        }

        Query query = new Query(Criteria.where("studentId").in(studentIds));
        Update update = new Update().set("companySupervisorId", supervisorId);

        var result = mongoTemplate.updateMulti(query, update, Student.class); // Apply update

        return result.getModifiedCount() > 0;
    }

    public String getStudentIdByMongoId(String mongoId) {
        // Query MongoDB for the student using Mongo-generated _id
        Query query = new Query(Criteria.where("_id").is(mongoId));
        Student student = mongoTemplate.findOne(query, Student.class);

        if (student != null) {
            return student.getStudentId();
        }

        return null;
    }
}
