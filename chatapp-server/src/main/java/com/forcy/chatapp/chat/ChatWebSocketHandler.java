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
