package com.internlink.internlink.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "students")
public class Student extends User {

    @Id
    private String studentId;
    private String major;
    private String facultySupervisorId;
    private String companySupervisorId;

    private String location;
    private List<String> skills;
    private List<Float> embedding;
    private int earnedHours;
    private int remainingHours;
    private double GPA;

    public Student() {
        super();
    }

    public Student(String email, String password, String name, String major, String facultySupervisorId,
            String companySupervisorId, String location, List<String> skills, List<Float> embedding,
            int earnedHours, int remainingHours, double GPA) {
        super(email, password, name);
        this.major = major;
        this.facultySupervisorId = facultySupervisorId;
        this.companySupervisorId = companySupervisorId;
        this.location = location;
        this.skills = skills;
        this.embedding = embedding;
        this.earnedHours = earnedHours;
        this.remainingHours = remainingHours;
        this.GPA = GPA;
    }

    // Getters and Setters
    @Override
    public String getId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getFacultySupervisorId() {
        return facultySupervisorId;
    }

    public void setFacultySupervisorId(String facultySupervisorId) {
        this.facultySupervisorId = facultySupervisorId;
    }

    public String getCompanySupervisorId() {
        return companySupervisorId;
    }

    public void setCompanySupervisorId(String companySupervisorId) {
        this.companySupervisorId = companySupervisorId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<Float> getEmbedding() {
        return embedding;
    }

    public void setEmbedding(List<Float> embedding) {
        this.embedding = embedding;
    }

    public int getEarnedHours() {
        return earnedHours;
    }

    public void setEarnedHours(int earnedHours) {
        this.earnedHours = earnedHours;
    }

    public double getGPA() {
        return GPA;
    }

    public void setGPA(double GPA) {
        this.GPA = GPA;
    }

    public int getRemainingHours() {
        return remainingHours;
    }

    public void setRemainingHours(int remainingHours) {
        this.remainingHours = remainingHours;
    }
}
