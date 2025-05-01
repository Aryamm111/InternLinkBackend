package com.internlink.internlink.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "interactions")
public class Interaction {

    @Id
    private String id;
    private String studentId;
    private String internshipId;
    private String interactionType; // viewed, applied, accepted
    private int interactionScore;
    private LocalDateTime timestamp;

    public Interaction(String studentId, String internshipId, String interactionType, int interactionScore) {
        this.studentId = studentId;
        this.internshipId = internshipId;
        this.interactionType = interactionType;
        this.interactionScore = interactionScore;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getInternshipId() {
        return internshipId;
    }

    public String getInteractionType() {
        return interactionType;
    }

    public int getInteractionScore() {
        return interactionScore;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setInternshipId(String internshipId) {
        this.internshipId = internshipId;
    }

    public void setInteractionType(String interactionType) {
        this.interactionType = interactionType;
    }

    public void setInteractionScore(int interactionScore) {
        this.interactionScore = interactionScore;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
