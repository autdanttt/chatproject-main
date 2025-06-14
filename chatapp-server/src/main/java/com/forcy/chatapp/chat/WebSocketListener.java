package com.forcy.chatapp.chat;

import com.forcy.chatapp.security.InMemorySessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketListener {

    private final Logger logger = LoggerFactory.getLogger(WebSocketListener.class);

    @Autowired private InMemorySessionManager sessionManager;

    private final Map<String,String> sessionIdToUsername = new ConcurrentHashMap<>();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        if (accessor.getUser() != null) {
            String username = accessor.getUser().getName();
            sessionManager.registerSession(username, sessionId);
            sessionIdToUsername.put(sessionId, username);
            logger.info("🔗 [WebSocket Connect] User '{}' connected with sessionId={}", username, sessionId);
        } else {
            logger.warn("⚠️ [WebSocket Connect] Không xác định được user cho sessionId={}", sessionId);
        }
    }


    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String username = sessionIdToUsername.remove(sessionId);
        if(username != null){
            sessionManager.removeSession(username);
            logger.info("❌ [WebSocket Disconnect] User '{}' disconnected from sessionId={}", username, sessionId);
        }else {
            logger.warn("⚠️ [WebSocket Disconnect] Không tìm thấy username cho sessionId={}", sessionId);
        }
    }
}
