package com.forcy.chatapp.chat;

import com.forcy.chatapp.entity.Chat;
import com.forcy.chatapp.entity.Message;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.message.MessageRepository;
import com.forcy.chatapp.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class ChatService {
    private ChatRepository chatRepository;
    private UserRepository userRepository;

    private MessageRepository messageRepository;

    public ChatService(ChatRepository chatRepository, UserRepository userRepository, MessageRepository messageRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    public List<Chat> getChats(Long userId){
       return chatRepository.findByUserId(userId);
    }

    public List<ChatResponse> getListChat(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String username = user.getUsername();

        return chatRepository.findByUserId(userId).stream()
                .map(chat -> {
                    User otherUser = chat.getUsers().stream()
                            .filter(u -> !u.getUsername().equals(username))
                            .findFirst()
                            .orElse(null);

                    Message lastMessage = messageRepository.findTopByChatIdOrderBySentAtDesc(chat.getId());

                    return ChatResponse.builder()
                            .chatId(chat.getId())
                            .otherUserId(otherUser != null ? otherUser.getId() : null)
                            .otherUsername(otherUser != null ? otherUser.getUsername() : "Unknown")
                            .lastMessage(lastMessage != null ? lastMessage.getContent() : "")
                            .lastMessageTime(lastMessage != null ? lastMessage.getSentAt() : null)
                            .build();
                }).toList();
    }

    public boolean isUserInChat(Long chatId, Long userId) {
        return chatRepository.existsByIdAndUserId(chatId, userId);
    }

    public Chat createChat(Long currentUserId, Long targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw new IllegalArgumentException("Cannot create chat with yourself");
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("Current user not found"));

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("Target user not found"));

        Optional<Chat> existingChat = chatRepository.findChatByTwoUserIds(currentUserId, targetUserId);
        log.info("Existing chat: {}", existingChat.isPresent());
        if (existingChat.isPresent()) {
            log.info("Existing chat ID: {}", existingChat.get().getId());
            return existingChat.get();
        }

        Chat chat = new Chat();
        currentUser.getChats().add(chat);
        targetUser.getChats().add(chat);
        chat.setUsers(List.of(currentUser, targetUser));
        return chatRepository.save(chat);
    }

    public void deleteChat(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

        boolean isUserInChat = chat.getUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));

        if (!isUserInChat) {
            throw new SecurityException("You are not allowed to delete this chat");
        }

        // Xóa liên kết giữa Chat và Users
        for (User user : chat.getUsers()) {
            user.getChats().remove(chat);
        }
        chat.getUsers().clear();

        chatRepository.delete(chat);
    }

}
