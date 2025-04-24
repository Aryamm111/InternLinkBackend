package com.internlink.internlink.controller;

import com.internlink.internlink.model.Report;
import com.internlink.internlink.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public List<Report> getAllReports() {
        return reportService.getAllReports();
    }

    @GetMapping("/{id}")
    public Report getReportById(@PathVariable int id) {
        return reportService.getReportById(id);
    }

    @PostMapping
    public Report addReport(@RequestBody Report report) {
        return reportService.addReport(report);
    }

    @PutMapping
    public Report updateReport(@RequestBody Report report) {
        return reportService.updateReport(report);
    }

    @DeleteMapping("/{id}")
    public boolean deleteReport(@PathVariable int id) {
        return reportService.deleteReport(id);
    }

    @PutMapping("/verify/{id}")
    public Report verifyReport(@PathVariable int id) {
        return reportService.verifyReport(id);
    }
}
