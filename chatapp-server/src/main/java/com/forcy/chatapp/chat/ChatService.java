package com.forcy.chatapp.chat;

import com.forcy.chatapp.entity.Chat;
import com.forcy.chatapp.entity.Message;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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


}
