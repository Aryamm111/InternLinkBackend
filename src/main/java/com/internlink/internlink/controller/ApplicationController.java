package com.internlink.internlink.controller;

import com.internlink.internlink.model.Application;
import com.internlink.internlink.service.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    //  استخدام Constructor Injection بدلاً من @Autowired
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    //  جلب جميع الطلبات
    @GetMapping
    public ResponseEntity<List<Application>> getAllApplications() {
        return ResponseEntity.ok(applicationService.getAllApplications());
    }

    //  جلب طلب معين باستخدام ID
    @GetMapping("/{id}")
    public ResponseEntity<Application> getApplicationById(@PathVariable String id) {
        Optional<Application> application = applicationService.getApplicationById(id);
        return application.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //  جلب جميع الطلبات لطالب معين
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Application>> getApplicationsByStudent(@PathVariable String studentId) {
        return ResponseEntity.ok(applicationService.getApplicationsByStudent(studentId));
    }

    // إنشاء طلب جديد
    @PostMapping
    public ResponseEntity<?> createApplication(@RequestBody Application application) {
        return applicationService.createApplication(application);
    }

    //  تحديث حالة الطلب
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateApplicationStatus(@PathVariable String id, @RequestParam String status) {
        try {
            Application.ApplicationStatus newStatus = Application.ApplicationStatus.valueOf(status.toUpperCase());
            Optional<Application> updatedApplication = applicationService.updateApplicationStatus(id, newStatus);
            return updatedApplication.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Application not found"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status value. Use PENDING, ACCEPTED, or REJECTED.");
        }
    }

    //  حذف طلب معين
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable String id) {
        boolean deleted = applicationService.deleteApplication(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
