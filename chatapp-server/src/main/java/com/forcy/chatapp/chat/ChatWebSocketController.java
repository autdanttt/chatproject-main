package com.forcy.chatapp.chat;

import com.forcy.chatapp.entity.Message;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.message.MessageMapper;
import com.forcy.chatapp.message.MessageRepository;
import com.forcy.chatapp.message.MessageResponse;
import com.forcy.chatapp.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ChatWebSocketController {
    private final Logger logger = LoggerFactory.getLogger(ChatWebSocketController.class);

    private SimpMessagingTemplate messagingTemplate;

    private MessageRepository messageRepository;


    private UserRepository userRepository;

    public ChatWebSocketController(SimpMessagingTemplate messagingTemplate, MessageRepository messageRepository, UserRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @MessageMapping("/ready")
    public void handleReadyMessage(@Payload ChatMessage chatMessage) { // Sử dụng Principal nếu cần xác thực
        String username = chatMessage.getSender(); // Hoặc dùng Principal nếu cấu hình
        logger.info("Received READY message from user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        List<Message> undeliveredMessages = messageRepository.findUndeliveredMessagesForUser(user.getId());
        logger.info("Found {} undelivered messages for user: {}", undeliveredMessages.size(), username);

        for (Message message : undeliveredMessages) {
            try {
                MessageResponse response = MessageMapper.toResponse(message, user.getId());
                messagingTemplate.convertAndSendToUser(username, "/queue/messages", response);
                logger.info("Sent undelivered message to user {}: {}", username, response.getContent());

                message.setDeliveredAt(LocalDateTime.now());
                messageRepository.save(message);
                logger.info("Updated deliveredAt for message ID: {}", message.getId());
            } catch (Exception e) {
                logger.error("Failed to send message to user {}: {}", username, e.getMessage(), e);
            }
        }
    }



}
