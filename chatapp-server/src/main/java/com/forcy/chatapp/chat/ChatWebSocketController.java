package com.forcy.chatapp.chat;

import com.forcy.chatapp.entity.Message;
import com.forcy.chatapp.entity.MessageDelivery;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.message.MessageDeliveryRepository;
import com.forcy.chatapp.message.MessageMapper;
import com.forcy.chatapp.message.MessageRepository;
import com.forcy.chatapp.message.MessageResponse;
import com.forcy.chatapp.user.UserNotFoundException;
import com.forcy.chatapp.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Controller
public class ChatWebSocketController {
    private final Logger logger = LoggerFactory.getLogger(ChatWebSocketController.class);

    private SimpMessagingTemplate messagingTemplate;

    private MessageRepository messageRepository;

    private MessageDeliveryRepository messageDeliveryRepository;


    private UserRepository userRepository;

    public ChatWebSocketController(SimpMessagingTemplate messagingTemplate, MessageRepository messageRepository, UserRepository userRepository, MessageDeliveryRepository messageDeliveryRepository) {
        this.messagingTemplate = messagingTemplate;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messageDeliveryRepository = messageDeliveryRepository;
    }

    @MessageMapping("/ready")
    public void handleReadyMessage(@Payload ChatMessage chatMessage) { // Sử dụng Principal nếu cần xác thực
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String email =chatMessage.getSender();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
        logger.info("Received READY message from user: {}", email);

        List<Message> undeliveredMessages = messageDeliveryRepository.findUndeliveredMessagesForUser(user.getId());
        logger.info("Found {} undelivered messages for user: {}", undeliveredMessages.size(), email);

        for (Message message : undeliveredMessages) {
            try {
                MessageResponse response = MessageMapper.toResponse(message, user.getId());
                messagingTemplate.convertAndSendToUser(email, "/queue/messages", response);
                logger.info("Sent undelivered message to user {}: {}", email, response.getContent());
//                message.setDeliveredAt(new Date());
                MessageDelivery messageDelivery = messageDeliveryRepository.findByMessageIdAndUserId(message.getId(), response.getToUserId());
//                messageRepository.save(message);
                messageDelivery.setDeliveredAt(new Date());
                messageDeliveryRepository.save(messageDelivery);
                logger.info("Updated deliveredAt for message ID: {}", message.getId());
            } catch (Exception e) {
                logger.error("Failed to send message to user {}: {}", email, e.getMessage(), e);
            }
        }
    }

    @MessageMapping("/video-call/hangup")
    public void handleCallEndedEvent(@Payload HangupMessage hangupMessage) {
        Long fromUserId =hangupMessage.getFromUserId();
        User user = userRepository.findById(fromUserId)
                .orElseThrow(() -> new UserNotFoundException(fromUserId));
        logger.info("Received READY message from user: {}",user.getEmail());

        User toUser = userRepository.findById(hangupMessage.getToUserId())
                .orElseThrow(() -> new UserNotFoundException(hangupMessage.getToUserId()));

        logger.info("Send hang up message to user: {}",toUser.getEmail());
        messagingTemplate.convertAndSendToUser(toUser.getEmail(), "/queue/video-call/peer", hangupMessage);
    }
}
