package com.forcy.chatapp.chat;

import com.forcy.chatapp.entity.Chat;
import com.forcy.chatapp.entity.Message;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.message.MessageMapper;
import com.forcy.chatapp.message.MessageResponse;
import com.forcy.chatapp.message.MessageService;
import com.forcy.chatapp.user.UserNotFoundException;
import com.forcy.chatapp.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Slf4j
@RestController
@RequestMapping("/chats")
public class ChatController {
    private final ChatService chatService;
    private final UserRepository userRepository;
    private final MessageService messageService;


    public ChatController(ChatService chatService, UserRepository userRepository, MessageService messageService) {
        this.chatService = chatService;
        this.userRepository = userRepository;
        this.messageService = messageService;
    }

    @GetMapping
    public List<ChatResponse> getChats(@RequestHeader("Authorization") String token) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
        return chatService.getListChat(user.getId());
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<?> getChatMessages(@PathVariable Long chatId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
        if(!chatService.isUserInChat(chatId, user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Message> messages = messageService.findMessagesByChatId(chatId);
        List<MessageResponse> responses = messages.stream().map(message ->
                MessageMapper.toResponse(message, user.getId())).toList();

        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<ChatResponse> createChat(@RequestBody CreateChatRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Chat chat = chatService.createChat(currentUser.getId(), request.getTargetUserId());

        User otherUser = chat.getUsers().stream()
                .filter(u -> !u.getId().equals(currentUser.getId()))
                .findFirst()
                .orElse(null);

        Message lastMessage = messageService.getLastMessageInChat(chat.getId());

        ChatResponse response = ChatResponse.builder()
                .chatId(chat.getId())
                .otherUserId(otherUser != null ? otherUser.getId() : null)
                .otherUsername(otherUser != null ? otherUser.getUsername() : "Unknown")
                .lastMessage(lastMessage != null ? lastMessage.getContent() : "")
                .lastMessageTime(lastMessage != null ? lastMessage.getSentAt() : null)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{chatId}")
    public ResponseEntity<?> deleteChat(@PathVariable Long chatId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        try {
            chatService.deleteChat(chatId, currentUser.getId());
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
