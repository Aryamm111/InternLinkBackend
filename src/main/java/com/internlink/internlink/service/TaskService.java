package com.internlink.internlink.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.internlink.internlink.model.Task;

@Service
public class TaskService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Task> getTasksForStudent(String studentId) {
        Query query = new Query(Criteria.where("assignedStudentId").is(studentId));
        return mongoTemplate.find(query, Task.class);
    }

    public Task createTask(Task task) {
        return mongoTemplate.save(task);
    }

    public Task getTaskById(String taskId) {

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(taskId));

        return mongoTemplate.findOne(query, Task.class);
    }

    public Task updateTask(Task task) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(task.getId()));

        Update update = new Update();
        update.set("completed", task.isCompleted());

        mongoTemplate.updateFirst(query, update, Task.class);

        return mongoTemplate.findOne(query, Task.class);
    }

    public Map<String, List<Task>> getStudentsTasksForSupervisor(String supervisorId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("supervisorId").is(supervisorId));

        List<Task> tasks = mongoTemplate.find(query, Task.class);

        return tasks.stream().collect(Collectors.groupingBy(Task::getAssignedStudentId));
    }

    public Map<String, Double> getTaskProgressSummary(String supervisorId) {
        // Fetch tasks for the given supervisor
        Query query = new Query();
        query.addCriteria(Criteria.where("supervisorId").is(supervisorId));

        List<Task> tasks = mongoTemplate.find(query, Task.class);

        // Count tasks by category
        long completed = tasks.stream().filter(Task::isCompleted).count();
        long pending = tasks.stream().filter(t -> !t.isCompleted() && t.getDueDate().isAfter(LocalDate.now())).count();
        long overdue = tasks.stream().filter(t -> !t.isCompleted() && t.getDueDate().isBefore(LocalDate.now())).count();

        // Calculate the total number of tasks
        long totalTasks = completed + pending + overdue;

        // Avoid division by zero
        if (totalTasks == 0) {
            return Map.of(
                    "completed", 0.0,
                    "pending", 0.0,
                    "overdue", 0.0);
        }

        // Calculate percentages
        double completedPercentage = (double) completed / totalTasks * 100;
        double pendingPercentage = (double) pending / totalTasks * 100;
        double overduePercentage = (double) overdue / totalTasks * 100;

        // Return percentages in a map
        return Map.of(
                "completed", completedPercentage,
                "pending", pendingPercentage,
                "overdue", overduePercentage);
    }

}
