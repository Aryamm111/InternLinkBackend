package com.internlink.internlink.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.internlink.internlink.model.Task;

@Service
public class TaskService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Task> getTasksForStudent(String studentId) {
        // Query MongoDB for tasks assigned to the given student ID
        Query query = new Query(Criteria.where("assignedStudentId").is(studentId));
        return mongoTemplate.find(query, Task.class);
    }
    // public Task createTask(Task task) {
    // return mongoTemplate.save(task);
    // }

    // public List<Task> getAllTasks() {
    // return mongoTemplate.findAll(Task.class);
    // }

    // public Optional<Task> getTaskById(String id) {
    // Task task = mongoTemplate.findById(id, Task.class);
    // return Optional.ofNullable(task);
    // }

    // public Optional<Task> updateTask(String id, Task updatedTask) {
    // Query query = new Query(Criteria.where("id").is(id));
    // Update update = new Update()
    // .set("title", updatedTask.getTitle())
    // .set("description", updatedTask.getDescription())
    // .set("status", updatedTask.getStatus())
    // .set("dueDate", updatedTask.getDueDate());

    // Task updated = mongoTemplate.findAndModify(query, update, Task.class);
    // return Optional.ofNullable(updated);
    // }

    // public Optional<Task> updateTaskStatus(String id, TaskStatus status) {
    // Query query = new Query(Criteria.where("id").is(id));
    // Update update = new Update().set("status", status);

    // Task updated = mongoTemplate.findAndModify(query, update, Task.class);
    // return Optional.ofNullable(updated);
    // }

    // public boolean deleteTask(String id) {
    // Query query = new Query(Criteria.where("id").is(id));
    // Task deletedTask = mongoTemplate.findAndRemove(query, Task.class);
    // return deletedTask != null;
    // }

    // public List<Task> getTasksByStudentId(String studentId) {
    // Query query = new Query(Criteria.where("assignedStudentId").is(studentId));
    // return mongoTemplate.find(query, Task.class);
    // }

    // public List<Task> getTasksBySupervisorId(String supervisorId) {
    // Query query = new Query(Criteria.where("supervisorId").is(supervisorId));
    // return mongoTemplate.find(query, Task.class);
    // }

    // public List<Task> getTasksByInternshipId(String internshipId) {
    // Query query = new Query(Criteria.where("internshipId").is(internshipId));
    // return mongoTemplate.find(query, Task.class);
    // }
}
