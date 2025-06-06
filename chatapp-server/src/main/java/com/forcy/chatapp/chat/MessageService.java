package com.forcy.chatapp.chat;

import com.forcy.chatapp.entity.Chat;
import com.forcy.chatapp.entity.Message;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private Logger logger = LoggerFactory.getLogger(MessageService.class);


    private MessageRepository messageRepository;

    private UserRepository userRepository;

    private ChatRepository chatRepository;

    private MessageProducer messageProducer;

    private InMemorySessionManager sessionManager;

    private SimpMessagingTemplate messagingTemplate;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository, ChatRepository chatRepository, MessageProducer messageProducer, InMemorySessionManager sessionManager, SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.messageProducer = messageProducer;
        this.sessionManager = sessionManager;

        this.messagingTemplate = messagingTemplate;
    }


    public void storeMessage(MessageRequest messageRequest) {
        logger.info("🔁 [storeMessage] Bắt đầu lưu tin nhắn từ userId={} đến userId={}", messageRequest.getFromUserId(), messageRequest.getToUserId());
        User fromUser = userRepository.findById(messageRequest.getFromUserId()).orElseThrow();

        Long toUserId = messageRequest.getToUserId();
        logger.info("✅ [storeMessage] Tìm thấy 2 user: from={}, to={}", fromUser.getUsername(), toUserId);
        if (toUserId == null) {
            throw new IllegalArgumentException("toUserId must not be null");
        }
        User toUser = userRepository.findById(toUserId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        Chat chat = chatRepository.findChatByTwoUserIds(fromUser.getId(), toUser.getId())
                .orElseGet(() -> {
                    logger.info("💬 [storeMessage] Tạo mới Chat giữa {} và {}", fromUser.getUsername(), toUser.getUsername());
                    Chat newChat = new Chat();

                    newChat.setUsers(List.of(fromUser, toUser));
                    return chatRepository.save(newChat);
                });



        Message message = MessageMapper.toEntity(messageRequest,fromUser,chat);
        messageRepository.save(message);

        //Gui toi Kafka de consumer xy ly WebSocket

        messageProducer.send(MessageMapper.toResponse(message,toUser.getId()));

        logger.info("🚀 [storeMessage] Gửi message tới Kafka topic cho userId={}", toUserId);
    }

    public void deliverMessage(MessageResponse messageResponse) {
        logger.info("📥 [deliverMessage] Nhận message từ Kafka: {}", messageResponse);

        User toUser = userRepository.findById(messageResponse.getToUserId()).orElseThrow();
        String sessionId = sessionManager.getSessionId(toUser.getUsername());
        logger.info("Delivering message to user: " + toUser.getUsername());
        logger.info("Session ID found: " + sessionId);
        if(sessionId != null){
            messagingTemplate.convertAndSendToUser(toUser.getUsername(),"/queue/messages", messageResponse);
            logger.info("Message sent to WebSocket for user: " + toUser.getUsername());

            Message message = messageRepository.findById(messageResponse.getMessageId()).orElseThrow(()->new RuntimeException("Message not found"));
            if(message.getDeliveredAt() == null){
                message.setDeliveredAt(LocalDateTime.now());
                messageRepository.save(message);
                logger.info("Message delivery time updated for message ID: {}" ,message.getId());
            }
        }else {
            logger.info("User {} is offline, cannot deliver message now.", toUser.getUsername());
        }
    }
}
