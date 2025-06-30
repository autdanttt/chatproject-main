package com.forcy.chatapp.message;

import com.forcy.chatapp.chat.ChatWebSocketHandler;
import com.forcy.chatapp.entity.Message;
import com.forcy.chatapp.entity.MessageDelivery;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.group.GroupMemberRepository;
import com.forcy.chatapp.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
public class MessageConsumer {

    private final MessageDeliveryRepository messageDeliveryRepository;
    private Logger logger = LoggerFactory.getLogger(MessageConsumer.class);
    private MessageRepository messageRepository;

    private ChatWebSocketHandler chatWebSocketHandler;

    private UserRepository userRepository;

    private GroupMemberRepository groupMemberRepository;

    public MessageConsumer(MessageRepository messageRepository, ChatWebSocketHandler chatWebSocketHandler, UserRepository userRepository, MessageDeliveryRepository messageDeliveryRepository, GroupMemberRepository groupMemberRepository) {
        this.messageRepository = messageRepository;
        this.chatWebSocketHandler = chatWebSocketHandler;
        this.userRepository = userRepository;
        this.messageDeliveryRepository = messageDeliveryRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    @KafkaListener(topics = "chat-messages", groupId = "chat-group")
    public void consume(MessageResponse response) {
        logger.info("📨 [KafkaConsumer] Nhận message từ topic: {}",response);
        deliverMessage(response);
    }

    @KafkaListener(topics = "group-messages", groupId = "group-chat")
    public void consumeGroupMessage(MessageResponse response) {
        deliverGroupMessage(response);
    }

    private void deliverGroupMessage(MessageResponse response) {
        logger.info("📨 [GroupConsumer] Nhận message nhóm từ Kafka: {}", response);
        Long groupId = response.getGroupId();
        Long senderId = response.getFromUserId();


        // Lấy danh sách thành viên nhóm (trừ sender)
        List<User> members = groupMemberRepository.findUsersByGroupId(groupId).stream()
                .filter(u -> !u.getId().equals(senderId))
                .toList();

        for (User member : members) {
            if(chatWebSocketHandler.isOnline(member.getUsername())){
                chatWebSocketHandler.sendMessageToUser(member.getUsername(), response);

                logger.info("Message sent to WebSocket for user: " + member.getUsername());
                logger.info("Message {} delivered",response);

                MessageDelivery messageDelivery = messageDeliveryRepository.findByMessageIdAndUserId(response.getMessageId(), member.getId());
                if(messageDelivery.getDeliveredAt() == null) {
                    messageDelivery.setDeliveredAt(new Date());
                    messageDeliveryRepository.save(messageDelivery);
                    logger.info("Message {} delivered",response.getMessageId());
                }
            }else {
                logger.info("User {} is offline", member.getUsername());
            }
        }
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

           // Message message = messageRepository.findById(messageResponse.getMessageId()).orElseThrow(() -> new RuntimeException("Message not found"));
//            if (message.getDeliveredAt() == null) {
//                message.setDeliveredAt(new Date());
//                messageRepository.save(message);
//                logger.info("Message delivery time updated for message ID: {}" , message.getId());
//            }
            MessageDelivery messageDelivery = messageDeliveryRepository.findByMessageIdAndUserId(messageResponse.getMessageId(), messageResponse.getToUserId());
            if (messageDelivery.getDeliveredAt() == null) {
                messageDelivery.setDeliveredAt(new Date());
                messageDeliveryRepository.save(messageDelivery);
                logger.info("Message {} delivered", messageResponse.getMessageId());
            }
        } else {
            logger.info("User {} is offline, cannot deliver message now.", username);
        }
    }
}
