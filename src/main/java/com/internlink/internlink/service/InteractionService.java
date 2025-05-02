package com.internlink.internlink.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.internlink.internlink.model.Interaction;
import com.internlink.internlink.model.Internship;

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

    public List<Internship> findInteractedInternships(String studentId) {
        // 1. Query Interaction collection for interactions by this student
        var query = new Query(Criteria.where("studentId").is(studentId));
        List<Interaction> interactions = mongoTemplate.find(query, Interaction.class);

        // 2. Extract internship IDs from interactions
        List<String> internshipIds = interactions.stream()
                .map(Interaction::getInternshipId)
                .collect(Collectors.toList());

        // 3. Fetch internships by IDs
        if (internshipIds.isEmpty()) {
            return new ArrayList<>();
        }

        Query internshipQuery = new Query(Criteria.where("_id").in(internshipIds));
        return mongoTemplate.find(internshipQuery, Internship.class);
    }

}
