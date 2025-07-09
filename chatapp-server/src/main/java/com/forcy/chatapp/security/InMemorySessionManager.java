package com.forcy.chatapp.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemorySessionManager{

    // email -> tập sessionId
    private final Map<String, Set<String>> userSessionMap = new ConcurrentHashMap<>();

    // email -> lastSeen timestamp
    private final Map<String, Long> lastSeenMap = new ConcurrentHashMap<>();

    /** Đăng ký session mới cho user */
    public void registerSession(String email, String sessionId) {
        userSessionMap
                .computeIfAbsent(email, k -> ConcurrentHashMap.newKeySet())
                .add(sessionId);
        updateLastSeen(email);
    }

    /** Xóa một session cụ thể của user */
    public void removeSession(String email, String sessionId) {
        Set<String> sessions = userSessionMap.get(email);
        if (sessions != null) {
            sessions.remove(sessionId);
            if (sessions.isEmpty()) {
                userSessionMap.remove(email);
                updateLastSeen(email);
            }
        }
    }

    /** Kiểm tra user còn online không (tức là còn ít nhất 1 session) */
    public boolean hasAnySession(String email) {
        Set<String> sessions = userSessionMap.get(email);
        return sessions != null && !sessions.isEmpty();
    }

    /** Kiểm tra user có đang hoạt động không */
    public boolean isUserOnline(String email) {
        return hasAnySession(email);
    }

    /** Cập nhật thời điểm hoạt động gần nhất */
    public void updateLastSeen(String email) {
        lastSeenMap.put(email, System.currentTimeMillis());
    }

    /** Trả về chuỗi trạng thái hoạt động */
    public String getStatus(String email) {
        if (isUserOnline(email)) return "Đang hoạt động";

        Long lastSeen = getLastSeen(email);
        if (lastSeen == 0) return "Không rõ";

        long minutesAgo = (System.currentTimeMillis() - lastSeen) / 60000;
        return "Hoạt động " + minutesAgo + " phút trước";
    }

    /** Lấy thời gian hoạt động cuối */
    public Long getLastSeen(String email) {
        return lastSeenMap.getOrDefault(email, 0L);
    }

    /** Trả về tất cả sessionId của user */
    public Set<String> getSessionIds(String email) {
        return userSessionMap.getOrDefault(email, Set.of());
    }
}
