package com.internlink.internlink.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.internlink.internlink.model.HRManager;
import com.internlink.internlink.model.User;
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

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getHRByEmail(@PathVariable String email) {
        User user = userService.findByEmail(email);
        if (user == null || !"HR_MANAGER".equals(user.getUserRole())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("HR Manager not found.");
        }
        return ResponseEntity.ok(user);
    }

}
