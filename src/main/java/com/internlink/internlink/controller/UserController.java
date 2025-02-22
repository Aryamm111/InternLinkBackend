package com.internlink.internlink.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.internlink.internlink.dto.LoginRequest;
import com.internlink.internlink.model.User;
import com.internlink.internlink.service.UserService;
import com.internlink.internlink.util.JwtUtil;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        String token = userService.login(username, password);
        if (token != null) {
            User user = userService.findByUsername(username);
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "userRole", user.getUserRole(),
                    "userId", user.getId()));
        } else {
            return ResponseEntity.status(401).body("Invalid credentials.");
        }
    }

    @GetMapping("/protected")
    public ResponseEntity<?> protectedEndpoint(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(400).body("Authorization token is missing or malformed.");
        }

        try {
            String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
            return ResponseEntity.ok("Access granted for user: " + username);
        } catch (Exception e) {
            return ResponseEntity.status(403).body("Invalid or expired token.");
        }
    }
}
