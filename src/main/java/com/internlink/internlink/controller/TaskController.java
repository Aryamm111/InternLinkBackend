package com.internlink.internlink.controller;

import com.internlink.internlink.model.Task;
import com.internlink.internlink.model.TaskStatus;
import com.internlink.internlink.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable String id) {
        Optional<Task> task = taskService.getTaskById(id);
        return task.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.ok(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable String id, @RequestBody Task updatedTask) {
        Optional<Task> task = taskService.updateTask(id, updatedTask);
        return task.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable String id, @RequestBody TaskStatus status) {
        Optional<Task> updatedTask = taskService.updateTaskStatus(id, status);
        return updatedTask.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        boolean deleted = taskService.deleteTask(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
