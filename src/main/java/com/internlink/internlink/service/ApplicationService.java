package com.internlink.internlink.service;

import com.internlink.internlink.model.Application;
import com.internlink.internlink.model.Application.ApplicationStatus;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {

    private final MongoTemplate mongoTemplate;

    public ApplicationService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    //  جلب جميع الطلبات
    public List<Application> getAllApplications() {
        return mongoTemplate.findAll(Application.class);
    }

    // جلب طلب معين باستخدام ID
    public Optional<Application> getApplicationById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, Application.class));
    }

    //  جلب جميع الطلبات لطالب معين
    public List<Application> getApplicationsByStudent(String studentId) {
        Query query = new Query(Criteria.where("studentId").is(studentId));
        return mongoTemplate.find(query, Application.class);
    }

    //  إنشاء طلب جديد مع التحقق من الوظيفة
    public ResponseEntity<?> createApplication(Application application) {
        InternshipOpportunity opportunity = mongoTemplate.findById(application.getOpportunityId(), InternshipOpportunity.class);

        if (opportunity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(" No jobs available, you cannot apply!");
        }

        application.setStatus(ApplicationStatus.PENDING);
        application.setStatusTimestamp(LocalDateTime.now());
        mongoTemplate.save(application);
        return ResponseEntity.status(HttpStatus.CREATED).body(application);
    }

    //  تحديث حالة الطلب
    public Optional<Application> updateApplicationStatus(String id, ApplicationStatus newStatus) {
        Query query = new Query(Criteria.where("applicationId").is(id));
        Update update = new Update()
            .set("status", newStatus)
            .set("statusTimestamp", LocalDateTime.now());

        Application updatedApplication = mongoTemplate.findAndModify(query, update, Application.class);
        return Optional.ofNullable(updatedApplication);
    }

    //  حذف طلب معين
    public boolean deleteApplication(String id) {
        Query query = new Query(Criteria.where("applicationId").is(id));
        return mongoTemplate.remove(query, Application.class).getDeletedCount() > 0;
    }
}

