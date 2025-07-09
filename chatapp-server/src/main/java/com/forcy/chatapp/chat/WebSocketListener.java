package com.forcy.chatapp.chat;

import com.forcy.chatapp.security.InMemorySessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketListener {



    private final Logger logger = LoggerFactory.getLogger(WebSocketListener.class);

    @Autowired private InMemorySessionManager sessionManager;

    @Autowired private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private final Map<String,String> sessionIdToEmail = new ConcurrentHashMap<>();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        if (accessor.getUser() != null) {
            String email = accessor.getUser().getName();
            sessionManager.registerSession(email, sessionId);
            sessionIdToEmail.put(sessionId,email);
            redisTemplate.opsForValue().set("user:"+ email+ ":status", "online", Duration.ofSeconds(60));
            logger.info("🔗 [WebSocket Connect] User '{}' connected with sessionId={}", email, sessionId);
        } else {
            logger.warn("⚠️ [WebSocket Connect] Không xác định được user cho sessionId={}", sessionId);
        }
    }


    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String email = sessionIdToEmail.remove(sessionId);

        if (email != null) {
            sessionManager.removeSession(email, sessionId);

            long now = System.currentTimeMillis();

            if (!sessionManager.hasAnySession(email)) {
//                redisTemplate.delete("user:" + email + ":status"); // ❗Xóa nếu không còn session nào

                redisTemplate.expire("user:" + email + ":status", Duration.ofSeconds(1)); // ⏱ Gần như "xóa mềm"
                redisTemplate.opsForValue().set("user:" + email + ":lastSeen", String.valueOf(now));
                logger.info("❌ [WebSocket Disconnect] User '{}' disconnected from sessionId={} and now offline", email, sessionId);
                // ✅ Thông báo cho watchers là mình đã offline
//                Set<String> watchers = redisTemplate.opsForSet().members("user:" + email + ":watchers");
//                if (watchers != null) {
//                    for (String watcher : watchers) {
//                        messagingTemplate.convertAndSendToUser(
//                                watcher,
//                                "/queue/heartbeat",
//                                new UserStatus(email, "offline")
//                        );
//                    }
//                }


//                // ✅ Gỡ A khỏi tất cả watcher list của người khác
//                Set<String> allKeys = redisTemplate.keys("user:*:watchers");
//                if (allKeys != null) {
//                    for (String key : allKeys) {
//                        redisTemplate.opsForSet().remove(key, email);
//                    }
//                }
            } else {
                logger.info("❌ [WebSocket Disconnect] User '{}' disconnected from sessionId={}, still has active sessions", email, sessionId);
            }
        } else {
            logger.warn("⚠️ [WebSocket Disconnect] Không tìm thấy username cho sessionId={}", sessionId);
        }
    }

}
