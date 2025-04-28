package com.internlink.internlink.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.internlink.internlink.model.Student;
import com.internlink.internlink.service.AuthService;
import com.internlink.internlink.service.EmbeddingService;
import com.internlink.internlink.service.StudentService;
import com.internlink.internlink.service.UserService;

import ai.djl.translate.TranslateException;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private AuthService authService;
    @Autowired
    private StudentService studentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('FACULTY_SUPERVISOR') or hasRole('COMPANY_SUPERVISOR')")
    public ResponseEntity<List<Student>> getAllStudentsForSupervisor() {
        String userId = authService.getAuthenticatedUserId();
        String supervisorRole = authService.getAuthenticatedUserRole();

        List<Student> students = supervisorRole.equals("ROLE_FACULTY_SUPERVISOR")
                ? studentService.getStudentsByFacultySupervisor(userId)
                : studentService.getStudentsByCompanySupervisor(userId);

        return ResponseEntity.ok(students);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerStudent(@RequestBody Student student) throws TranslateException {
        try {
            student.setPassword(passwordEncoder.encode(student.getPassword()));
            student.setUserRole("STUDENT"); // Set role

            String text = student.getMajor() + " ".repeat(3) + student.getLocation() + " ".repeat(2)
                    + student.getSkills();
            List<Float> embedding = embeddingService.generateEmbedding(text);
            student.setEmbedding(embedding);

            // Save the student object to the database
            studentService.register(student);
            return ResponseEntity.ok("Student registered successfully!");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> updateStudent(@RequestBody Student updatedStudent)
            throws TranslateException {

        // Validate the authenticated user
        String authenticatedStudentId = authService.getAuthenticatedUserId();

        // Pass the update request to the service layer
        Student updated = studentService.updateStudent(authenticatedStudentId, updatedStudent);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
        }

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{studentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> deleteStudent(@PathVariable String studentId) {
        String authenticatedStudentId = authService.getAuthenticatedUserId();

        if (authenticatedStudentId == null || !authenticatedStudentId.equals(studentId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not authorized to delete this profile");
        }

        Student student = studentService.getStudentById(studentId);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
        }

        studentService.deleteStudent(studentId);
        return ResponseEntity.ok("Student deleted successfully");
    }

    @GetMapping("/{studentId}/name")
    public ResponseEntity<?> getStudentName(@PathVariable String studentId) {
        Student student = studentService.getStudentById(studentId);
        return (student != null) ? ResponseEntity.ok(student.getName())
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
    }

    @PostMapping("/{studentId}/add")
    @PreAuthorize("hasRole('FACULTY_SUPERVISOR')")
    public ResponseEntity<?> assignFacultySupervisor(@PathVariable String studentId) {
        String supervisorId = authService.getAuthenticatedUserId();

        if (supervisorId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Student student = studentService.assignFacultySupervisor(studentId, supervisorId);

        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
        }

        return ResponseEntity.ok("Student assigned successfully!");
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('HR_MANAGER')")
    public ResponseEntity<?> assignSupervisorToStudents(
            @RequestParam String supervisorId,
            @RequestBody List<String> studentIds) {

        studentService.assignSupervisorToStudents(supervisorId, studentIds);
        return ResponseEntity.ok("Students assigned successfully.");
    }

}
