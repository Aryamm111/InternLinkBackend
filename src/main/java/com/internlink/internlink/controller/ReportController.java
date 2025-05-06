package com.internlink.internlink.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.internlink.internlink.model.Report;
import com.internlink.internlink.model.Student;
import com.internlink.internlink.service.AuthService;
import com.internlink.internlink.service.ReportService;
import com.internlink.internlink.service.StudentService;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("api/reports")

public class ReportController {
    @Autowired
    private ReportService reportService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private AuthService authService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> uploadReport(@RequestParam("file") MultipartFile file) {
        try {
            String studentId = authService.getAuthenticatedUserId();
            Student student = studentService.getStudentById(studentId);

            // Check if student has no supervisors
            if ((student.getFacultySupervisorId().isEmpty() && student.getCompanySupervisorId().isEmpty())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("You don't have a supervisor yet. You cannot upload a report.");
            }

            // If supervisor exists, proceed
            reportService.uploadReport(studentId, file);
            return ResponseEntity.ok("Report uploaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to upload report: " + e.getMessage());
        }
    }

    @GetMapping("/studentreport")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<Report>> getMyReports() {
        String studentId = authService.getAuthenticatedUserId();
        List<Report> reports = reportService.viewReport(studentId);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/supervisor")
    @PreAuthorize("hasAnyRole('FACULTY_SUPERVISOR', 'COMPANY_SUPERVISOR')")
    public ResponseEntity<List<Report>> getReportsForSupervisor() {
        String supervisorId = authService.getAuthenticatedUserId();
        return ResponseEntity.ok(reportService.getReportsBySupervisorId(supervisorId));
    }

    @PutMapping("/{reportId}/verify")
    @PreAuthorize("hasRole('COMPANY_SUPERVISOR')")
    public ResponseEntity<String> verifyReport(@PathVariable String reportId) {
        try {
            reportService.verifyReport(reportId);
            return ResponseEntity.ok("Report verified successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to verify report: " + e.getMessage());
        }
    }
}
