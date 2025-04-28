package com.internlink.internlink.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.internlink.internlink.model.CompanySupervisor;
import com.internlink.internlink.service.AuthService;
import com.internlink.internlink.service.CompanySupervisorService;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/companysupervisors")
public class CompanySupervisorController {

    @Autowired
    private AuthService authService;

    @Autowired
    private CompanySupervisorService companySupervisorService;

    @PostMapping("/register")
    public ResponseEntity<?> registerCompanySupervisor(@RequestBody CompanySupervisor companySupervisor) {

        return companySupervisorService.registerCompanySupervisor(companySupervisor);
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('HR_MANAGER')")
    public ResponseEntity<List<CompanySupervisor>> getSupervisorsForHR() {
        String hrManagerId = authService.getAuthenticatedUserId();
        List<CompanySupervisor> supervisors = companySupervisorService.getByHRManagerId(hrManagerId);
        return ResponseEntity.ok(supervisors);
    }

}
