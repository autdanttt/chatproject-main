package com.forcy.chatapp.message;

import com.forcy.chatapp.chat.ChatWebSocketHandler;
import com.forcy.chatapp.entity.Message;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MessageConsumer {

    private Logger logger = LoggerFactory.getLogger(MessageConsumer.class);
    private MessageRepository messageRepository;

    private ChatWebSocketHandler chatWebSocketHandler;

    private UserRepository userRepository;

    public MessageConsumer(MessageRepository messageRepository, ChatWebSocketHandler chatWebSocketHandler, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.chatWebSocketHandler = chatWebSocketHandler;
        this.userRepository = userRepository;

    }

    @KafkaListener(topics = "chat-messages", groupId = "chat-group")
    public void consume(MessageResponse response) {
        logger.info("📨 [KafkaConsumer] Nhận message từ topic: {}",response);
        deliverMessage(response);
    }

    public void deliverMessage(MessageResponse messageResponse) {
        logger.info("📥 [deliverMessage] Nhận message từ Kafka: {}", messageResponse);
        User toUser = userRepository.findById(messageResponse.getToUserId()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        String username = toUser.getUsername();

        logger.info("Delivering message to user: " + username);
        if (chatWebSocketHandler.isOnline(username)) {
            chatWebSocketHandler.sendMessageToUser(username, messageResponse);
            logger.info("Message sent to WebSocket for user: " + username);
            logger.info("Message {} delivered", messageResponse);

            Message message = messageRepository.findById(messageResponse.getMessageId()).orElseThrow(() -> new RuntimeException("Message not found"));
            if (message.getDeliveredAt() == null) {
                message.setDeliveredAt(LocalDateTime.now());
                messageRepository.save(message);
                logger.info("Message delivery time updated for message ID: {}" , message.getId());
            }
        } else {
            logger.info("User {} is offline, cannot deliver message now.", username);
        }
    }
}
