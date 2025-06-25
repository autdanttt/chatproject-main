package com.forcy.chatapp.message;

import com.forcy.chatapp.chat.*;
import com.forcy.chatapp.entity.Chat;
import com.forcy.chatapp.entity.ChatGroup;
import com.forcy.chatapp.entity.Message;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.group.ChatGroupRepository;
import com.forcy.chatapp.group.GroupNotFoundException;
import com.forcy.chatapp.user.UserNotFoundException;
import com.forcy.chatapp.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MessageService {

    private final ChatGroupRepository chatGroupRepository;
    private Logger logger = LoggerFactory.getLogger(MessageService.class);


    private MessageRepository messageRepository;

    private UserRepository userRepository;

    private ChatRepository chatRepository;

    private MessageProducer messageProducer;

    private ChatWebSocketHandler chatWebSocketHandler;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository, ChatRepository chatRepository, MessageProducer messageProducer, @Lazy ChatWebSocketHandler chatWebSocketHandler, ChatGroupRepository chatGroupRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.messageProducer = messageProducer;
        this.chatWebSocketHandler = chatWebSocketHandler;
        this.chatGroupRepository = chatGroupRepository;
    }

    public Message storeGroupMessage(GroupMessageRequest groupMessageRequest) {

        User fromUser = userRepository.findById(groupMessageRequest.getFromUserId())
                .orElseThrow(()->new UserNotFoundException(groupMessageRequest.getFromUserId()));

        Long toGroupId = groupMessageRequest.getToGroupId();

        ChatGroup chatGroup = chatGroupRepository.findById(toGroupId).orElseThrow(
                ()-> new GroupNotFoundException("Group not found with id " + toGroupId)
        );

        Message message = new Message();
        message.setUser(fromUser);
        message.setGroup(chatGroup);
        message.setType(groupMessageRequest.getMessageType());
        message.setContent(groupMessageRequest.getContent());
        message.setSentAt(new Date());

        messageRepository.save(message);


        /// ////////////////
        return null;
    }


    public Message storeMessage(MessageRequest messageRequest) {
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

//        ChatResponse chatResponse = new ChatResponse();
//        chatResponse.setChatId(chat.getId());
//        chatResponse.setOtherUsername(toUser.getUsername());
//        chatResponse.setLastMessage(message.getContent());
//        chatResponse.setLastMessageTime(message.getSentAt());
//
//        try {
//            if (chatWebSocketHandler.isOnline(fromUser.getUsername())) {
//                chatWebSocketHandler.sendMessageToUser(fromUser.getUsername(), chatResponse);
//            }
//        } catch (Exception e) {
//            logger.error("Failed to send ChatResponse to UserA: {}", e.getMessage());
//        }
        return message;
    }
    public Message getLastMessageInChat(Long chatId) {
        return messageRepository.findTopByChatIdOrderBySentAtDesc(chatId);
    }


    public List<Message> findMessagesByChatId(Long chatId) {
        return messageRepository.findByChatId(chatId);
    }

}
