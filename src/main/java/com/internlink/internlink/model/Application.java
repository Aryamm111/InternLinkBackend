package com.internlink.internlink.model;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
@Document(collection = "Application")
public class Application {

    @Indexed(unique = true)
    private String applicationId;
    private String studentId;
    private String opportunityId; //  إضافة معرف الوظيفة
    private String opportunityName;
    private ApplicationStatus status; //  تحسين حالة الطلب
    private LocalDateTime statusTimestamp; // تحسين الطابع الزمني

    public enum ApplicationStatus {
        PENDING, ACCEPTED, REJECTED;
    }

    public Application() {}

    public Application(String applicationId, String studentId, String opportunityId, String opportunityName, ApplicationStatus status, LocalDateTime statusTimestamp) {
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.opportunityId = opportunityId;
        this.opportunityName = opportunityName;
        this.status = status;
        this.statusTimestamp = statusTimestamp;
    }

    //  Getters and Setters
    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getOpportunityId() { return opportunityId; }
    public void setOpportunityId(String opportunityId) { this.opportunityId = opportunityId; }

    public String getOpportunityName() { return opportunityName; }
    public void setOpportunityName(String opportunityName) { this.opportunityName = opportunityName; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public LocalDateTime getStatusTimestamp() { return statusTimestamp; }
    public void setStatusTimestamp(LocalDateTime statusTimestamp) { this.statusTimestamp = statusTimestamp; }
}
