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
        if (user1Id.equals(user2Id)) {
            throw new IllegalArgumentException("Cannot start a conversation with yourself.");
        }

        if (!userService.userExistsByEmail(user1Id) || !userService.userExistsByEmail(user2Id)) {
            throw new RuntimeException("One or both users do not exist.");
        }

        Query query = new Query();
        query.addCriteria(new Criteria().andOperator(
                Criteria.where("participantIds").all(user1Id, user2Id),
                Criteria.where("participantIds").size(2)));

        Conversation existing = mongoTemplate.findOne(query, Conversation.class, "conversations");

        if (existing != null) {
            return existing;
        }

        Conversation newConversation = new Conversation(List.of(user1Id, user2Id));
        mongoTemplate.save(newConversation, "conversations");
        return newConversation;
    }

    public List<Conversation> getUserConversations(String userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("participantIds").in(userId));
        return mongoTemplate.find(query, Conversation.class, "conversations");
    }

}
