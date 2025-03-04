package com.internlink.internlink.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/tasks")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getTasksForStudent() {

        String Id = authService.getAuthenticatedUserId();

        // Use StudentService to fetch the custom studentId
        String studentId = studentService.getStudentIdByMongoId(Id);

        if (studentId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
        }

        List<Task> tasks = taskService.getTasksForStudent(studentId);

        if (tasks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No tasks found for this student");
        }

        return ResponseEntity.ok(tasks);
    }

    // @GetMapping("/{id}")
    // public ResponseEntity<Task> getTaskById(@PathVariable String id) {
    // Optional<Task> task = taskService.getTaskById(id);
    // return task.map(ResponseEntity::ok)
    // .orElseGet(() -> ResponseEntity.notFound().build());
    // }

    // @PostMapping
    // public ResponseEntity<Task> createTask(@RequestBody Task task) {
    // String supervisorId = getAuthenticatedUserId(); // Get the logged-inuser's ID
    // if (supervisorId == null || !isCompanySupervisor()) {
    // return ResponseEntity.status(403).body(null); // Forbidden if not acompany
    // supervisor
    // }

    // task.setSupervisorId(supervisorId); // Assign supervisor ID to task
    // Task createdTask = taskService.createTask(task);
    // return ResponseEntity.ok(createdTask);
    // }

    // @PutMapping("/{id}")
    // public ResponseEntity<Task> updateTask(@PathVariable String id, @RequestBody
    // Task updatedTask) {
    // Optional<Task> task = taskService.updateTask(id, updatedTask);
    // return task.map(ResponseEntity::ok)
    // .orElseGet(() -> ResponseEntity.notFound().build());
    // }

    // @PutMapping("/status/{id}")
    // public ResponseEntity<Task> updateTaskStatus(@PathVariable String id,
    // @RequestBody TaskStatus status) {
    // Optional<Task> updatedTask = taskService.updateTaskStatus(id, status);
    // return updatedTask.map(ResponseEntity::ok)
    // .orElseGet(() -> ResponseEntity.notFound().build());
    // }

    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> deleteTask(@PathVariable String id) {
    // boolean deleted = taskService.deleteTask(id);
    // return deleted ? ResponseEntity.noContent().build() :
    // ResponseEntity.notFound().build();
    // }
}
