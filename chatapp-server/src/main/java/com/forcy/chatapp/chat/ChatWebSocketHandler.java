package com.forcy.chatapp.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class ChatWebSocketHandler{

    private SimpMessagingTemplate messagingTemplate;


    private InMemorySessionManager sessionManager;

    public ChatWebSocketHandler(SimpMessagingTemplate messagingTemplate, InMemorySessionManager sessionManager) {
        this.messagingTemplate = messagingTemplate;
        this.sessionManager = sessionManager;
    }

    public void sendMessageToUser(String username, Object response) {
        if(sessionManager.isUserOnline(username)){
            String destination = "/user/" + username + "/queue/messages";
            messagingTemplate.convertAndSendToUser(username, "/queue/messages", response);

        }
    }

    public boolean isOnline(String username) {
        return sessionManager.isUserOnline(username);
    }


}
