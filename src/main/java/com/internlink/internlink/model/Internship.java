package com.internlink.internlink.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "internships")
public class Internship {

    @Id
    private String id;
    private String title;
    private String company;
    private String description;
    private String location;
    private int duration;
    private Date startDate;
    private List<String> majors;
    private List<String> requiredSkills;
    private List<Float> embedding;
    private List<String> students;
    private int maxStudents;
    private String uploadedBy;
    private byte[] internshipPlanFile;
    private byte[] internshipImage;

    public Internship() {
    }

    public Internship(String uploadedBy, String title, String company, String location,
            String description, Date startDate, int duration,
            List<String> majors, List<String> requiredSkills, int maxStudents,
            byte[] internshipPlanFile, byte[] internshipImage, List<Float> embedding) {
        this.uploadedBy = uploadedBy;
        this.title = title;
        this.company = company;
        this.location = location;
        this.description = description;
        this.startDate = startDate;
        this.duration = duration;
        this.majors = majors;
        this.requiredSkills = requiredSkills;
        this.maxStudents = maxStudents;
        this.internshipPlanFile = internshipPlanFile;
        this.internshipImage = internshipImage;
        this.embedding = embedding;
    }

    // Getters and setters
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getMajors() {
        return majors;
    }

    public void setMajors(List<String> majors) {
        this.majors = majors;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getStartDate() {
        return startDate;
    }

    public List<Float> getEmbedding() {
        return embedding;
    }

    public void setEmbedding(List<Float> embedding) {
        this.embedding = embedding;
    }

    public List<String> getStudents() {
        return students;
    }

    public void setStudents(List<String> students) {
        this.students = students;
    }

    public byte[] getInternshipPlanFile() {
        return internshipPlanFile;
    }

    public void setInternshipPlanFile(byte[] internshipPlanFile) {
        this.internshipPlanFile = internshipPlanFile;
    }

    public byte[] getInternshipImage() {
        return internshipImage;
    }

    public void setInternshipImage(byte[] internshipImage) {
        this.internshipImage = internshipImage;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(int maxStudents) {
        this.maxStudents = maxStudents;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }
}
