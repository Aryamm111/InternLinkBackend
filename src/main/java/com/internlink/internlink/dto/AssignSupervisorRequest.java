package com.internlink.internlink.dto;

import java.util.List;

public class AssignSupervisorRequest {
    private List<String> studentIds;

    public List<String> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<String> studentIds) {
        this.studentIds = studentIds;
    }
}
