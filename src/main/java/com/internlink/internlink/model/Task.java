package com.internlink.internlink.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tasks")
public class Task {

    @Id
    private String id;
    private String title;
    private String description;
    private boolean completed; // Change status to boolean completed
    private String assignedStudentId;
    private String supervisorId;
    private LocalDate dueDate;

    public Task() {
        this.completed = false; // Default is not completed
    }

    public Task(String title, String description, String assignedStudentId, String supervisorId, LocalDate dueDate) {
        this.title = title;
        this.description = description;
        this.completed = false; // Default to not completed
        this.assignedStudentId = assignedStudentId;
        this.supervisorId = supervisorId;
        this.dueDate = dueDate;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getAssignedStudentId() {
        return assignedStudentId;
    }

    public void setAssignedStudentId(String assignedStudentId) {
        this.assignedStudentId = assignedStudentId;
    }

    public String getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(String supervisorId) {
        this.supervisorId = supervisorId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
