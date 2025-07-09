package com.forcy.chatapp.chat;

import com.forcy.chatapp.security.InMemorySessionManager;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class ChatWebSocketHandler{

    private SimpMessagingTemplate messagingTemplate;


    private InMemorySessionManager sessionManager;

    public ChatWebSocketHandler(SimpMessagingTemplate messagingTemplate, InMemorySessionManager sessionManager) {
        this.messagingTemplate = messagingTemplate;
        this.sessionManager = sessionManager;
    }

    public void sendMessageToUser(String email, Object response) {
        if(sessionManager.isUserOnline(email)){
            String destination = "/user/" + email + "/queue/messages";
            messagingTemplate.convertAndSendToUser(email, "/queue/messages", response);

        }
    }

    public boolean isOnline(String email) {
        return sessionManager.isUserOnline(email);
    }
}
