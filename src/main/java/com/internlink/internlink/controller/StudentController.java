package com.internlink.internlink.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.internlink.internlink.model.Student;
import com.internlink.internlink.service.StudentService;
import com.internlink.internlink.util.JwtUtil;

import io.jsonwebtoken.Claims;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StudentService studentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<?> getStudentsBySupervisor(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Claims claims = jwtUtil.extractClaims(token);

        String supervisorRole = claims.get("role", String.class);
        String supervisorId = claims.getSubject();
        String userId = claims.getSubject();

        if (!supervisorRole.equalsIgnoreCase("facultySupervisor") &&
                !supervisorRole.equalsIgnoreCase("companySupervisor")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(userId);
        }

        List<Student> students = supervisorRole.equalsIgnoreCase("facultySupervisor")
                ? studentService.getStudentsByFacultySupervisor(supervisorId)
                : studentService.getStudentsByCompanySupervisor(supervisorId);

        return ResponseEntity.ok(students);
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<?> getStudentById(@PathVariable String studentId) {
        Student student = studentService.getStudentById(studentId);
        return (student != null) ? ResponseEntity.ok(student)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerStudent(@RequestBody Student student) {
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        student.setUserRole("STUDENT");
        studentService.register(student);
        return ResponseEntity.ok("Student registered successfully!");
    }

    @PutMapping("/{studentId}")
    public ResponseEntity<?> updateStudent(@PathVariable String studentId, @RequestBody Student updatedStudent) {
        Student student = studentService.updateStudent(studentId, updatedStudent);
        return (student != null) ? ResponseEntity.ok(student)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<String> deleteStudent(@PathVariable String studentId) {
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
    public ResponseEntity<?> assignFacultySupervisor(
            @PathVariable String studentId,
            @RequestHeader("Authorization") String authHeader) {

        // Extract supervisor ID from the JWT token
        String token = authHeader.substring(7);
        Claims claims = jwtUtil.extractClaims(token);

        String supervisorRole = claims.get("role", String.class);
        String supervisorId = claims.getSubject();

        // Check if the user is a faculty supervisor
        if (!supervisorRole.equalsIgnoreCase("facultySupervisor")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only faculty supervisors can assign students.");
        }

        // Assign the faculty supervisor to the student
        Student student = studentService.assignFacultySupervisor(studentId, supervisorId);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
        }

        return ResponseEntity.ok("Student assigned to faculty supervisor successfully!");
    }
}
