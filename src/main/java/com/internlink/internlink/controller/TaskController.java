package com.internlink.internlink.controller;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.internlink.internlink.model.Task;
import com.internlink.internlink.service.AuthService;
import com.internlink.internlink.service.StudentService;
import com.internlink.internlink.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private AuthService authService;
    @Autowired
    private StudentService studentService;

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT') or hasRole('COMPANY_SUPERVISOR')")
    public ResponseEntity<?> getTasksForStudent(@RequestParam(required = false) String studentId) {
        String authenticatedUserId = authService.getAuthenticatedUserId();
        String authenticatedUserRole = authService.getAuthenticatedUserRole();

        System.out.println("authenticatedUserId: " + authenticatedUserId);
        System.out.println("studentId before: " + studentId);

        if (authenticatedUserRole.equals("COMPANY_SUPERVISOR")) {
            if (studentId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Student ID is required for supervisors");
            }

            // Fetch tasks for the student and filter by supervisorId
            List<Task> tasks = taskService.getTasksForStudent(studentId);
            tasks = tasks.stream()
                    .filter(task -> authenticatedUserId.equals(task.getSupervisorId())) // Validate supervisorId
                    .collect(Collectors.toList());

            // If no tasks are found or they don't belong to the supervisor, forbid access
            if (tasks.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Student is not under this supervisor");

            }
            return ResponseEntity.ok(tasks);
        } else if (studentId == null) {
            studentId = authenticatedUserId; // Default to authenticated student ID
        }

        System.out.println("studentId after: " + studentId);

        // Fetch tasks for the student
        List<Task> tasks = taskService.getTasksForStudent(studentId);

        if (tasks.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList()); // Return an empty list if no tasks are found
        }
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('COMPANY_SUPERVISOR')")
    public ResponseEntity<?> createTask(@RequestBody Task task) {

        String supervisorId = authService.getAuthenticatedUserId();

        if (supervisorId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: User not found");
        }

        // Validate the assigned student's ID
        if (task.getAssignedStudentId() == null || task.getAssignedStudentId().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Student ID is required");
        }

        if (!studentService.existsById(task.getAssignedStudentId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
        }

        // Set the supervisor's ID and create the task
        task.setSupervisorId(supervisorId);
        Task createdTask = taskService.createTask(task);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{taskId}/complete")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> toggleTaskCompletion(@PathVariable("taskId") String taskId) {
        // Get authenticated student's ID directly
        String studentId = authService.getAuthenticatedUserId();

        if (studentId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
        }

        // Fetch the task by ID
        Task task = taskService.getTaskById(taskId);

        if (task == null || !task.getAssignedStudentId().equals(studentId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Task not found or not assigned to the student");
        }

        // Toggle the completion status
        task.setCompleted(!task.isCompleted());

        // Update the task in the database
        Task updatedTask = taskService.updateTask(task);
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping("/supervisor/studentstasks")
    @PreAuthorize("hasRole('COMPANY_SUPERVISOR')")
    public ResponseEntity<?> getStudentsTasksForSupervisor() {
        String supervisorId = authService.getAuthenticatedUserId();

        if (supervisorId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: User not found");
        }

        // Fetch tasks grouped by students
        return ResponseEntity.ok(taskService.getStudentsTasksForSupervisor(supervisorId));
    }

    @GetMapping("/progress")
    @PreAuthorize("hasRole('COMPANY_SUPERVISOR')")
    public ResponseEntity<?> getTaskProgressForSupervisor() {
        String supervisorId = authService.getAuthenticatedUserId();

        if (supervisorId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: User not found");
        }

        return ResponseEntity.ok(taskService.getTaskProgressSummary(supervisorId));
    }

}
