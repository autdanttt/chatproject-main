package com.forcy.chatapp.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemorySessionManager{


    private final Map<String ,String> userSessionMap = new ConcurrentHashMap<>();

    public void registerSession(String email, String sessionId) {
        userSessionMap.put(email, sessionId);
    }

    public void removeSession(String email) {
        userSessionMap.remove(email);
    }

    public boolean isUserOnline(String email) {
        return userSessionMap.containsKey(email);
    }

    public String getSessionId(String email) {
        return userSessionMap.get(email);
    }

}
