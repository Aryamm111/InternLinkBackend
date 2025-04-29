package com.internlink.internlink.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.internlink.internlink.model.Message;
import com.internlink.internlink.service.MessageService;

@Controller
public class ChatWebSocketController {

    @Autowired
    private MessageService messageService;

    @MessageMapping("/chat.send")
    @SendTo("/topic/messages")
    public Message send(Message message) {
        return messageService.sendMessage(
                message.getConversationId(),
                message.getSenderId(),
                message.getContent());
    }
}