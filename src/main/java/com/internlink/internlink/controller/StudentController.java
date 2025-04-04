package com.internlink.internlink.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.internlink.internlink.dto.AssignSupervisorRequest;
import com.internlink.internlink.model.Student;
import com.internlink.internlink.service.AuthService;
import com.internlink.internlink.service.StudentService;
import com.internlink.internlink.service.UserService;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/students")
public class StudentController {

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
    public ResponseEntity<?> getStudentsBySupervisor() {
        String userId = authService.getAuthenticatedUserId(); // Get authenticated user ID
        String supervisorRole = authService.getAuthenticatedUserRole(); // Get user role

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        List<Student> students = supervisorRole.equals("ROLE_FACULTY_SUPERVISOR")
                ? studentService.getStudentsByFacultySupervisor(userId)
                : studentService.getStudentsByCompanySupervisor(userId);

        return ResponseEntity.ok(students);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerStudent(@RequestBody Student student) {
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        student.setUserRole("STUDENT");
        studentService.register(student);
        return ResponseEntity.ok("Student registered successfully!");
    }

    @PutMapping("/{studentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> updateStudent(@PathVariable String studentId, @RequestBody Student updatedStudent) {

        String authenticatedStudentId = authService.getAuthenticatedUserId();
        String id = studentService.getStudentIdByMongoId(authenticatedStudentId);

        if (id == null || !id.equals(studentId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not authorized to update this profile" + studentId + " " + id);
        }

        Student student = studentService.updateStudent(id, updatedStudent);
        return (student != null) ? ResponseEntity.ok(student)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
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
        String supervisorId = authService.getAuthenticatedUserId(); // Get authenticated user ID

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
    public ResponseEntity<?> assignCompanySupervisor(
            @RequestParam String supervisorId,
            @RequestBody AssignSupervisorRequest request) {
        List<String> studentIds = request.getStudentIds();

        if (studentIds == null || studentIds.isEmpty()) {
            return ResponseEntity.badRequest().body("No student IDs provided");
        }

        boolean success = studentService.assignCompanySupervisorToStudents(supervisorId, studentIds);

        return success
                ? ResponseEntity.ok("Supervisor assigned successfully!")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("No students found");
    }

}
