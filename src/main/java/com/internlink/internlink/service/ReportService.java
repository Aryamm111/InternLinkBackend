package com.internlink.internlink.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.internlink.internlink.model.Report;
import com.internlink.internlink.model.Student;

@Service
public class ReportService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private StudentService studentService;

    public void uploadReport(String studentId, MultipartFile file) throws IOException {
        Student student = studentService.getStudentById(studentId);

        // Save file
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get("uploads/reports/" + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        // Always create a new report — don't check if one exists
        Report report = new Report();
        report.setStudentId(studentId);
        report.setFacultySupervisorId(student.getFacultySupervisorId());
        report.setCompanySupervisorId(student.getCompanySupervisorId());
        report.setFileUrl("http://localhost:8081/uploads/reports/" + fileName);
        report.setVerified(false);
        report.setStudentName(student.getName());

        report.setSubmissionDate(LocalDateTime.now());

        mongoTemplate.save(report);
    }

    public List<Report> viewReport(String studentId) {
        Query query = new Query(Criteria.where("studentId").is(studentId));
        return mongoTemplate.find(query, Report.class);
    }

    public List<Report> getReportsBySupervisorId(String supervisorId) {
        Query query;
        query = new Query(new Criteria().orOperator(
                Criteria.where("facultySupervisorId").is(supervisorId),
                Criteria.where("companySupervisorId").is(supervisorId)));
        return mongoTemplate.find(query, Report.class);
    }

    public void verifyReport(String reportId) {
        Query query = new Query(Criteria.where("id").is(reportId));
        Update update = new Update().set("isVerified", true);
        mongoTemplate.updateFirst(query, update, Report.class);
    }
}
