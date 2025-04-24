package com.internlink.internlink.service;

import com.internlink.internlink.model.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Report> getAllReports() {
        return mongoTemplate.findAll(Report.class);
    }

    public Report getReportById(int reportId) {
        return mongoTemplate.findById(reportId, Report.class);
    }

    public Report addReport(Report report) {
        return mongoTemplate.save(report);
    }

    public Report updateReport(Report report) {
        return mongoTemplate.save(report);
    }

    public boolean deleteReport(int reportId) {
        Report report = getReportById(reportId);
        if (report != null) {
            mongoTemplate.remove(report);
            return true;
        }
        return false;
    }

    public Report verifyReport(int reportId) {
        Report report = getReportById(reportId);
        if (report != null) {
            report.setVerified();
            return mongoTemplate.save(report);
        }
        return null;
    }
}
