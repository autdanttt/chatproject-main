package com.forcy.chatapp.chat;

import com.forcy.chatapp.ErrorDTO;
import com.forcy.chatapp.entity.Chat;
import com.forcy.chatapp.entity.Message;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.message.MessageMapper;
import com.forcy.chatapp.message.MessageResponse;
import com.forcy.chatapp.message.MessageService;
import com.forcy.chatapp.user.UserNotFoundException;
import com.forcy.chatapp.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
        return chatService.getListChat(user.getId());
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<?> getChatMessages(@PathVariable Long chatId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
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
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
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
                .otherUserFullName(otherUser != null ? otherUser.getFullName() : "Unknown")
                .imageUrl(otherUser.getAvatarUrl())
                .lastMessage(lastMessage != null ? lastMessage.getContent() : "")
                .lastMessageTime(lastMessage != null ? lastMessage.getSentAt() : null)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{chatId}")
    public ResponseEntity<?> deleteChat(@PathVariable Long chatId, HttpServletRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found " + email));

        try {
            chatService.deleteChat(chatId, currentUser.getId());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Xóa chat thành công!");
            return ResponseEntity.ok(response);

        } catch (SecurityException e) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setTimestamp(new Date());
            errorDTO.setStatus(HttpStatus.FORBIDDEN.value());
            errorDTO.setPath(request.getRequestURI());
            errorDTO.addError("Bạn không có quyền xoá cuộc trò chuyện này");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDTO);

        } catch (EntityNotFoundException e) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setTimestamp(new Date());
            errorDTO.setStatus(HttpStatus.NOT_FOUND.value());
            errorDTO.setPath(request.getRequestURI());
            errorDTO.addError(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDTO);
        }
    }
}
