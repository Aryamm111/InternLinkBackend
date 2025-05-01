package com.internlink.internlink.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.internlink.internlink.model.Conversation;

@Service
public class ConversationService {

    private final MongoTemplate mongoTemplate;
    private final UserService userService;

    @Autowired
    public ConversationService(MongoTemplate mongoTemplate, UserService userService) {
        this.mongoTemplate = mongoTemplate;
        this.userService = userService;
    }

    public Conversation getOrCreateConversation(String user1Id, String user2Id) {
        // Prevent users from starting a conversation with themselves
        if (user1Id.equals(user2Id)) {
            throw new IllegalArgumentException("Cannot start a conversation with yourself.");
        }

        // Ensure both users exist in the system
        if (!userService.userExistsByEmail(user1Id) || !userService.userExistsByEmail(user2Id)) {
            throw new RuntimeException("One or both users do not exist.");
        }

        // Query to check if a conversation between these users already exists
        Query query = new Query();
        query.addCriteria(new Criteria().andOperator(
                Criteria.where("participantIds").all(user1Id, user2Id),
                Criteria.where("participantIds").size(2)));

        Conversation existing = mongoTemplate.findOne(query, Conversation.class, "conversations");

        // Return existing conversation if found, otherwise create a new one
        if (existing != null) {
            return existing;
        }

        Conversation newConversation = new Conversation(List.of(user1Id, user2Id));
        mongoTemplate.save(newConversation, "conversations");
        return newConversation;
    }

    public List<Conversation> getUserConversations(String userId) {
        // Retrieve all conversations where the user is a participant
        Query query = new Query();
        query.addCriteria(Criteria.where("participantIds").in(userId));
        return mongoTemplate.find(query, Conversation.class, "conversations");
    }
}
