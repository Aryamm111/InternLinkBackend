package com.internlink.internlink.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Enables WebSocket message handling
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // Defines a topic-based message broker for subscribers
        config.setApplicationDestinationPrefixes("/app"); // Prefix for messages sent by clients
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // Defines WebSocket endpoint
                .setAllowedOrigins("http://localhost:5173") // Allows requests from the system's frontend
                .withSockJS(); // Enables SockJS fallback for clients without native WebSocket support
    }
}
