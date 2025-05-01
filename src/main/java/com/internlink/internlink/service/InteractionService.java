package com.internlink.internlink.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.internlink.internlink.model.Interaction;

@Service
public class InteractionService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void saveInteraction(String studentId, String internshipId, String interactionType) {
        int score = switch (interactionType) {
            case "applied" -> 3;
            case "accepted" -> 5;
            default -> 1;
        };

        Interaction interaction = new Interaction(studentId, internshipId, interactionType, score);
        mongoTemplate.save(interaction);
    }

}
