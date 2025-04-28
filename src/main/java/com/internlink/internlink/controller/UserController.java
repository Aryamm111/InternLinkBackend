package com.internlink.internlink.controller;
//April 24, at 6:52

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.internlink.internlink.dto.LoginRequest;
import com.internlink.internlink.model.ResetToken;
import com.internlink.internlink.model.Student;
import com.internlink.internlink.model.User;
import com.internlink.internlink.service.EmbeddingService;
import com.internlink.internlink.service.MailService;
import com.internlink.internlink.service.UserService;

import ai.djl.translate.TranslateException;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private EmbeddingService embeddingService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        System.out.println("Login endpoint reached!");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            User user = (User) authentication.getPrincipal();

            if ("STUDENT".equalsIgnoreCase(user.getUserRole())) {
                Query studentQuery = new Query(Criteria.where("email").is(loginRequest.getEmail()));
                Student student = mongoTemplate.findOne(studentQuery, Student.class, "students");

                if (student != null && (student.getEmbedding() == null || student.getEmbedding().isEmpty())) {
                    try {
                        String text = student.getMajor() + "   " + student.getLocation() + "  " + student.getSkills();
                        List<Float> embedding = embeddingService.generateEmbedding(text);
                        student.setEmbedding(embedding);
                        mongoTemplate.save(student, "students");
                        System.out.println("Embedding generated for student: " + student.getEmail());
                    } catch (TranslateException e) {
                        System.out.println("Embedding generation failed: " + e.getMessage());
                    }
                }
            }

            return ResponseEntity.ok().build();

        } catch (BadCredentialsException ex) {
            System.out.println("Invalid credentials: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        } catch (Exception ex) {
            System.out.println("Unexpected error: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/session")
    public ResponseEntity<?> getSessionUser(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"No active session\"}");
        }

        User sessionUser = (User) auth.getPrincipal();
        System.out.println("Session User: " + sessionUser.getUsername());

        // ✨ INSTEAD OF mongoTemplate, use your service
        User freshUser = userService.findByEmail(sessionUser.getEmail());
        if (freshUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"User not found\"}");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("userId", freshUser.getId());
        response.put("userEmail", freshUser.getUsername());
        response.put("userName", freshUser.getName());
        response.put("role", freshUser.getUserRole());

        if (freshUser instanceof Student student) {
            response.put("major", student.getMajor());
            response.put("gpa", student.getGPA());
            response.put("skills", student.getSkills());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        String token = UUID.randomUUID().toString().substring(0, 6); // 6-digit code

        ResetToken resetToken = new ResetToken(email, token, Instant.now().plus(Duration.ofMinutes(30)));
        mongoTemplate.save(resetToken, "resetTokens");

        mailService.sendResetLink(email, token); // ✅ send only code

        return ResponseEntity.ok("Reset code sent to your email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String newPassword = payload.get("newPassword");

        Query query = new Query(Criteria.where("token").is(token));
        ResetToken resetToken = mongoTemplate.findOne(query, ResetToken.class, "resetTokens");

        if (resetToken == null || resetToken.getExpiry().isBefore(Instant.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
        }

        String email = resetToken.getEmail();
        boolean userUpdated = false;

        // Only HR and Company Supervisors
        List<String> allowedCollections = List.of("hrmanagers", "companySupervisors");

        for (String collection : allowedCollections) {
            Query emailQuery = new Query(Criteria.where("email").is(email));
            User found = mongoTemplate.findOne(emailQuery, User.class, collection);

            if (found != null) {
                found.setPassword(passwordEncoder.encode(newPassword));
                mongoTemplate.save(found, collection);
                userUpdated = true;
                break;
            }
        }

        if (!userUpdated) {
            // If it's a Student/Faculty or email not found
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Password reset not allowed. Please contact your university administrator.");
        }

        mongoTemplate.remove(resetToken); // remove token after use

        return ResponseEntity.ok("Password successfully reset.");
    }

    @GetMapping("/protected")
    public ResponseEntity<?> protectedEndpoint() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User) {
            String username = ((User) principal).getUsername();
            return ResponseEntity.ok("Access granted for user: " + username);
        }

        return ResponseEntity.status(403).body("Access denied.");
    }
}
