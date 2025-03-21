package com.internlink.internlink.controller;

import com.internlink.internlink.model.Report;
import com.internlink.internlink.model.Task;
import com.internlink.internlink.model.Student;
import com.internlink.internlink.service.CompanySupervisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/company-supervisor")
public class CompanySupervisorController {

    @Autowired
    private CompanySupervisorService companySupervisorService;

    @GetMapping("/reports")
    public List<Report> getAllReports() {
        return companySupervisorService.getAllReports();
    }

    @PostMapping("/reports/{reportId}/verify")
    public boolean verifyReport(@PathVariable String reportId) {
        return companySupervisorService.verifyReport(reportId);
    }

    @PostMapping("/tasks/assign")
    public void assignTask(@RequestParam String studentId, @RequestBody Task task) {
        companySupervisorService.assignTask(studentId, task);
    }

    @GetMapping("/tasks/progress/{studentId}")
    public List<Task> getTaskProgress(@PathVariable String studentId) {
        return companySupervisorService.getTaskProgress(studentId);
    }

    @GetMapping("/students/{studentId}")
    public Student getStudentInfo(@PathVariable String studentId) {
        return companySupervisorService.getStudentInfo(studentId);
    }
}
