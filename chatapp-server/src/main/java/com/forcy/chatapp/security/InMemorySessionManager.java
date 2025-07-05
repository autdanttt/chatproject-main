package com.forcy.chatapp.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemorySessionManager{


    private final Map<String ,String> userSessionMap = new ConcurrentHashMap<>();

    private final Map<String, Long> lastSeenMap = new ConcurrentHashMap<>();

    public void registerSession(String email, String sessionId) {
        userSessionMap.put(email, sessionId);
        updateLastSeen(email);
    }

    public void updateLastSeen(String email) {
        lastSeenMap.put(email, System.currentTimeMillis());
    }

    public void removeSession(String email) {
        userSessionMap.remove(email);
        updateLastSeen(email);
    }

    public boolean isUserOnline(String email) {
        return userSessionMap.containsKey(email);
    }

    public String getStatus(String email) {
        if (isUserOnline(email)) return "Đang hoạt động";

        Long lastSeen = getLastSeen(email);
        if (lastSeen == 0) return "Không rõ";

        long minutesAgo = (System.currentTimeMillis() - lastSeen) / 60000;
        return "Hoạt động " + minutesAgo + " phút trước";
    }

    public Long getLastSeen(String email) {
        return lastSeenMap.getOrDefault(email, 0L);
    }

    public String getSessionId(String email) {
        return userSessionMap.get(email);
    }

}
