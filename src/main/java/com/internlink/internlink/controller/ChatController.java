package com.internlink.internlink.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.internlink.internlink.model.Conversation;
import com.internlink.internlink.model.Message;
import com.internlink.internlink.service.ConversationService;
import com.internlink.internlink.service.MessageService;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private MessageService messageService;

    // Get all conversations for user
    @GetMapping("/conversations/{userId}")
    public List<Conversation> getConversations(@PathVariable String userId) {
        return conversationService.getUserConversations(userId);
    }

    // Create or fetch existing conversation
    @PostMapping("/conversation")
    public Conversation getOrCreateConversation(@RequestBody Map<String, String> payload) {
        String user1Id = payload.get("user1");
        String user2Id = payload.get("user2");
        return conversationService.getOrCreateConversation(user1Id, user2Id);
    }

    // Get messages in a conversation
    @GetMapping("/messages/{conversationId}")
    public List<Message> getMessages(@PathVariable String conversationId) {
        return messageService.getMessages(conversationId);
    }

    // Send message
    @PostMapping("/message")
    public Message sendMessage(@RequestBody Map<String, String> payload) {
        String conversationId = payload.get("conversationId");
        String senderId = payload.get("senderId");
        String content = payload.get("content");
        return messageService.sendMessage(conversationId, senderId, content);
    }
}
