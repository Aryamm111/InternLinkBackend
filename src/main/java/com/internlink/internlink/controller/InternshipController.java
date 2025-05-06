package com.internlink.internlink.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.internlink.internlink.model.Internship;
import com.internlink.internlink.service.AuthService;
import com.internlink.internlink.service.InteractionService;
import com.internlink.internlink.service.InternshipService;
import com.internlink.internlink.service.StudentService;

@RestController
@RequestMapping("/api/internships")
public class InternshipController {
    @Autowired
    private AuthService authService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private InternshipService internshipService;
    @Autowired
    private InteractionService interactionService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('HR_MANAGER')")
    public ResponseEntity<String> createInternship(
            @RequestPart("title") String title,
            @RequestPart("company") String company,
            @RequestPart("location") String location,
            @RequestPart("description") String description,
            @RequestPart("startDate") String startDateStr,
            @RequestPart("duration") String durationStr,
            @RequestPart("majors") String majorsJson,
            @RequestPart("requiredSkills") String skillsJson,
            @RequestPart("maxStudents") String maxStudentsStr,
            @RequestPart(value = "internshipPlanFile", required = false) MultipartFile planFile,
            @RequestPart(value = "internshipImage", required = false) MultipartFile image) {
        try {
            String managerId = authService.getAuthenticatedUserId();
            Internship internship = internshipService.buildInternshipFromRequest(
                    null,
                    managerId, title, company, location, description, startDateStr,
                    durationStr, majorsJson, skillsJson, maxStudentsStr,
                    planFile, image, true);

            internshipService.createInternship(internship);
            return ResponseEntity.ok("Internship created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error creating internship: " + e.getMessage());
        }
    }

    @GetMapping("/recommend")
    @PreAuthorize("hasRole('STUDENT')") // Ensures only students can access this endpoint
    public ResponseEntity<Map<String, Object>> recommendInternships(
            @RequestParam String studentId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int limit) {
        try {
            // Fetch student's embedding vector for recommendation calculations
            List<Float> studentEmbedding = studentService.getStudentEmbedding(studentId);
            System.out
                    .println("Student embedding fetched: " + (studentEmbedding != null ? "Success" : "Null or empty"));
            // Retrieve student's major for relevance in recommendations
            String studentMajor = studentService.getStudentMajor(studentId);
            // Get recommended internships based on student's embedding and major
            List<Internship> allInternships = internshipService.getRecommendedInternships(studentEmbedding,
                    studentMajor);
            // Implement pagination logic
            int startIndex = (page - 1) * limit;
            int endIndex = Math.min(startIndex + limit, allInternships.size());
            List<Internship> internshipsForPage = allInternships.subList(startIndex, endIndex);

            // Prepare response map with internship data and pagination details
            Map<String, Object> response = new HashMap<>();
            response.put("internships", internshipsForPage);
            int totalPages = Math.max(1, (int) Math.ceil((double) allInternships.size() / limit));
            response.put("totalPages", totalPages);
            response.put("currentPage", page);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Handle unexpected errors gracefully
            System.err.println("Error in recommendInternships: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('HR_MANAGER')")
    public ResponseEntity<String> updateInternship(
            @PathVariable String id,
            @RequestPart("title") String title,
            @RequestPart("company") String company,
            @RequestPart("location") String location,
            @RequestPart("description") String description,
            @RequestPart("startDate") String startDateStr,
            @RequestPart("duration") String durationStr,
            @RequestPart("majors") String majorsJson,
            @RequestPart("requiredSkills") String skillsJson,
            @RequestPart("maxStudents") String maxStudentsStr,
            @RequestPart(value = "internshipPlanFile", required = false) MultipartFile planFile,
            @RequestPart(value = "internshipImage", required = false) MultipartFile image) {
        try {
            Internship existing = internshipService.getInternshipById(id);
            if (existing == null) {
                return ResponseEntity.notFound().build();
            }

            Internship updated = internshipService.buildInternshipFromRequest(
                    existing, // pass existing internship here
                    existing.getUploadedBy(),
                    title, company, location, description, startDateStr,
                    durationStr, majorsJson, skillsJson, maxStudentsStr,
                    planFile, image, false);

            updated.setStatus("active");
            updated.setId(id);
            internshipService.updateInternship(id, updated);
            return ResponseEntity.ok("Internship updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error updating internship: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('STUDENT')")
    public List<Internship> searchInternships(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        String studentId2 = authService.getAuthenticatedUserId();

        if (studentId2 == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        return internshipService.searchInternships(title, studentId2, page, size);
    }

    @PreAuthorize("hasRole('HR_MANAGER')")
    @GetMapping("/uploaded")
    public ResponseEntity<List<Internship>> getUploadedInternships(@RequestParam String hrManagerId) {
        try {
            List<Internship> internships = internshipService.getUploadedInternships(hrManagerId);
            return ResponseEntity.ok(internships);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Internship> getInternshipById(@PathVariable String id) {
        try {
            Internship internship = internshipService.getInternshipById(id);
            if (internship != null) {
                String studentId = authService.getAuthenticatedUserId();
                String role = authService.getAuthenticatedUserRole();
                if (role != null && role.toUpperCase().contains("STUDENT") && studentId != null) {
                    boolean alreadyViewed = interactionService.interactionExists(studentId, id, "viewed");
                    if (!alreadyViewed) {
                        interactionService.saveInteraction(studentId, id, "viewed");
                    }
                }

                return ResponseEntity.ok(internship);

            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error fetching internship details: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('HR_MANAGER')")
    public ResponseEntity<String> deleteInternship(@PathVariable String id) {
        boolean deleted = internshipService.softDeleteInternship(id);
        if (deleted) {
            return ResponseEntity.ok("Internship deleted (soft delete) successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Internship not found.");
        }
    }

}
