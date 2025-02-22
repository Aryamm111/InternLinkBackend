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
    private TaskStatus status;
    private String assignedStudentId;
    private String supervisorId;
    private String internshipId;
    private LocalDate dueDate;

    public Task() {
        this.status = TaskStatus.TO_DO;
    }

    public Task(String title, String description, String assignedStudentId, String supervisorId, String internshipId,
            LocalDate dueDate) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.TO_DO;
        this.assignedStudentId = assignedStudentId;
        this.supervisorId = supervisorId;
        this.internshipId = internshipId;
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

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
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

    public String getInternshipId() {
        return internshipId;
    }

    public void setInternshipId(String internshipId) {
        this.internshipId = internshipId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
