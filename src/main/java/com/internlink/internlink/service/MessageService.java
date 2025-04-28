package com.internlink.internlink.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.internlink.internlink.model.Message;

@Service
public class MessageService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserService userService;

    public List<Message> getMessages(String conversationId) {
        Query query = new Query(Criteria.where("conversationId").is(conversationId))
                .with(Sort.by(Sort.Direction.ASC, "timestamp"));
        return mongoTemplate.find(query, Message.class);
    }

    public Message sendMessage(String conversationId, String senderId, String content) {
        Message message = new Message(conversationId, senderId, content);
        return mongoTemplate.save(message);
    }
}
