package com.internlink.internlink.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "students")
public class Student extends User {

    @Indexed(unique = true)
    private String studentId;
    private String major;
    private String university;
    private String facultySupervisorId;
    private String companySupervisorId;

    public Student() {
        super();
    }

    public Student(String email, String password, String name, String major, String university) {
        super(email, password, name);
        this.major = major;
        this.university = university;
    }

    public String getStudentId() {
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

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getFacultySupervisorId() {
        return this.facultySupervisorId;
    }

    public void setFacultySupervisorId(String facultySupervisorId) {
        this.facultySupervisorId = facultySupervisorId;
    }

    public String getCompanySupervisorId() {
        return this.companySupervisorId;
    }

    public void setCompanySupervisorId(String companySupervisorId) {
        this.companySupervisorId = companySupervisorId;
    }

}
