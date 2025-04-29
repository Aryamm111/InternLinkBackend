package com.internlink.internlink.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.internlink.internlink.model.HRManager;
import com.internlink.internlink.service.UserService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/hrmanagers")
public class HRManagerController {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerHR(@RequestBody HRManager hRManager) {
        if (userService.userExistsByEmail(hRManager.getEmail())) {
            throw new IllegalArgumentException("Email already exists!");
        }
        hRManager.setPassword(passwordEncoder.encode(hRManager.getPassword()));
        hRManager.setUserRole("HR_MANAGER");
        mongoTemplate.save(hRManager, "hrmanagers");
        return ResponseEntity.ok("hRManager registered successfully!");
    }

}
