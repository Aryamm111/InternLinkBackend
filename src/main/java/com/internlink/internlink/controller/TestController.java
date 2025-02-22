package com.internlink.internlink.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/ping")
    public ResponseEntity<String> pingMongo() {
        try {
            mongoTemplate.getDb().listCollectionNames().first();
            return ResponseEntity.ok("MongoDB is working!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("MongoDB connection failed: " + e.getMessage());
        }
    }
}
