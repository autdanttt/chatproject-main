package com.forcy.chatapp.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Set;

public class RedisKeyExpirationListener implements MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(RedisKeyExpirationListener.class);
    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    public RedisKeyExpirationListener(RedisTemplate<String, String> redisTemplate,
                                      SimpMessagingTemplate messagingTemplate) {
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
    }


    @Override
    public void onMessage(Message message, byte[] pattern) {
        logger.info("🔥 [Redis Expire Event] Key expired: {}", message.toString());
        String expiredKey = message.toString();

        // Chỉ xử lý key user:{email}:status
        if (expiredKey.matches("^user:.+:status$")) {
            String email = expiredKey.split(":")[1];
            long now = System.currentTimeMillis();

            // Cập nhật lastSeen
            redisTemplate.opsForValue().set("user:" + email + ":lastSeen", String.valueOf(now));
            logger.info("⌛ [Redis Expire] '{}' expired → set lastSeen={}", expiredKey, now);

            // Gửi notify cho watchers
            Set<String> watchers = redisTemplate.opsForSet().members("user:" + email + ":watchers");
            if (watchers != null) {
                for (String watcher : watchers) {
                    logger.info("Send status to watcher: {}", watcher);
                    messagingTemplate.convertAndSendToUser(
                            watcher,
                            "/queue/status",
                            new StatusNotification(email, "offline", now)
                    );
                }
            }
        }
    }
}
