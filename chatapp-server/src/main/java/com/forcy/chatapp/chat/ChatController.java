package com.forcy.chatapp.chat;

import com.forcy.chatapp.entity.Chat;
import com.forcy.chatapp.entity.Message;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/chats")
public class ChatController {

    private ChatService chatService;

    private UserRepository userRepository;

    private MessageService messageService;

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


}
