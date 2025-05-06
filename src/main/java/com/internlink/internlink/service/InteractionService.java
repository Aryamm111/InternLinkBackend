package com.internlink.internlink.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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

    public boolean interactionExists(String studentId, String internshipId, String type) {
        Query query = new Query();
        query.addCriteria(Criteria.where("studentId").is(studentId)
                .and("internshipId").is(internshipId)
                .and("interactionType").is(type));
        return mongoTemplate.exists(query, Interaction.class, "interactions");
    }

}
