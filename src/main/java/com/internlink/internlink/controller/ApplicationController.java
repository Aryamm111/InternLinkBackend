package com.internlink.internlink.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.internlink.internlink.model.Application;
import com.internlink.internlink.model.Internship;
import com.internlink.internlink.model.Student;
import com.internlink.internlink.service.ApplicationService;
import com.internlink.internlink.service.AuthService;
import com.internlink.internlink.service.InternshipService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
    @Autowired
    private AuthService authService;
    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private InternshipService internshipService;

    // Allows a student to apply to a specific internship (for student)
    @PostMapping("/{internshipId}/apply")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> apply(
            @PathVariable String internshipId,
            @RequestParam String studentId,
            @RequestParam String internshipTitle,
            @RequestParam MultipartFile applicationLetter,
            @RequestParam(value = "academicRecord", required = false) MultipartFile academicRecord,
            @RequestParam(value = "cv", required = false) MultipartFile cv,
            @RequestParam String skills) {
        try {
            // Pass all files to the service method
            applicationService.saveApplication(internshipId, studentId, internshipTitle, applicationLetter,
                    academicRecord, cv, skills);
            return ResponseEntity.ok("Application submitted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the application");
        }
    }

    // Retrieves all applications submitted by a specific student (for student)
    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<Application>> getStudentApplications(Authentication authentication) {
        try {
            String studentId = authService.getAuthenticatedUserId();
            List<Application> applications = applicationService.findApplicationsByStudentId(studentId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Retrieves all student applicants for a specific internship (for hr)
    @GetMapping("/{internshipId}/applicants")
    public ResponseEntity<List<Application>> getApplicants(@PathVariable String internshipId) {
        List<Application> applications = applicationService.getApplicationsForInternship(internshipId);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/accepted-students")
    @PreAuthorize("hasRole('HR_MANAGER')")
    public ResponseEntity<List<Student>> getAcceptedStudents() {
        String hrManagerId = authService.getAuthenticatedUserId();
        List<Student> students = applicationService.getAcceptedStudentsForHr(hrManagerId);
        return ResponseEntity.ok(students);
    }

    // Updates the status of a specific application
    @PostMapping("/{applicationId}/updatestatus")
    public ResponseEntity<String> updateApplicationStatus(
            @PathVariable String applicationId,
            @RequestParam String status) {
        try {
            Application application = applicationService.getApplicationById(applicationId);

            if ("Accepted".equalsIgnoreCase(status)) {
                Internship internship = internshipService.getInternshipById(application.getInternshipId());

                if (internship.getStudents().size() >= internship.getMaxStudents()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Cannot accept more students. Maximum limit reached.");
                }

                internship.getStudents().add(application.getStudentId());
                internshipService.saveInternship(internship);

            }

            applicationService.updateStatus(applicationId, status);

            return ResponseEntity.ok("Application status updated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the application status: " + applicationId);
        }
    }

}
