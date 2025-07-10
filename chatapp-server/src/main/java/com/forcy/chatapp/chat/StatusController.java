package com.forcy.chatapp.chat;

import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.redis.StatusNotification;
import com.forcy.chatapp.security.InMemorySessionManager;
import com.forcy.chatapp.user.UserNotFoundException;
import com.forcy.chatapp.user.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/status")
public class StatusController {

    private final InMemorySessionManager sessionManager;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public StatusController(InMemorySessionManager sessionManager, UserRepository userRepository, RedisTemplate redisTemplate) {
        this.sessionManager = sessionManager;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

//    @GetMapping("/{other-user-id}")
//    public ResponseEntity<UserStatus> getStatus(@PathVariable("other-user-id") Long otherUserId) {
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
//        User otherUser = userRepository.findById(otherUserId).orElseThrow(() -> new UserNotFoundException("User not found: " + otherUserId));
//        String status = sessionManager.getStatus(otherUser.getEmail());
//        return ResponseEntity.ok(new UserStatus(otherUser.getEmail(), status));
//    }

    @GetMapping("/{other-user-id}")
    public ResponseEntity<StatusNotification> getStatus(@PathVariable("other-user-id") Long otherUserId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));

        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + otherUserId));

        String otherEmail = otherUser.getEmail();

        // Kiểm tra status trong Redis
        Boolean isOnline = redisTemplate.hasKey("user:" + otherEmail + ":status");
        String status = Boolean.TRUE.equals(isOnline) ? "online" : "offline";

        // Lấy thời gian lastSeen
        String lastSeenStr = redisTemplate.opsForValue().get("user:" + otherEmail + ":lastSeen");
        long lastSeen = lastSeenStr != null ? Long.parseLong(lastSeenStr) : 0L;

        StatusNotification notification = new StatusNotification(otherEmail, status, lastSeen);
        return ResponseEntity.ok(notification);
    }
}
