package com.forcy.chatapp.chat;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemorySessionManager{


    private final Map<String ,String> userSessionMap = new ConcurrentHashMap<>();

    public void registerSession(String username, String sessionId) {
        userSessionMap.put(username, sessionId);
    }

    public void removeSession(String username) {
        userSessionMap.remove(username);
    }

    public boolean isUserOnline(String username) {
        return userSessionMap.containsKey(username);
    }

    public String getSessionId(String username) {
        return userSessionMap.get(username);
    }

}
