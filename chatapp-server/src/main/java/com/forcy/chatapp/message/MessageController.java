package com.forcy.chatapp.message;

import com.forcy.chatapp.entity.Message;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.token.RefreshTokenRepository;
import com.forcy.chatapp.user.UserNotFoundException;
import com.forcy.chatapp.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
@Validated
public class MessageController {

    private MessageService messageService;
    private UserRepository userRepository;

    public MessageController(MessageService messageService, UserRepository userRepository) {
        this.messageService = messageService;
        this.userRepository = userRepository;
    }


    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody MessageRequest messageRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if(!messageRequest.getFromUserId().equals(user.getId())) {
            throw new AccessDeniedException("Your account is not allowed to send messages");
        }

        Message message = messageService.storeMessage(messageRequest);

        MessageResponse messageResponse = MessageMapper.toResponse(message, messageRequest.getToUserId());

        return new ResponseEntity<>(messageResponse, HttpStatus.CREATED);
    }


    @PostMapping("/group")
    public ResponseEntity<?> sendGroupMessage(@RequestBody @Valid GroupMessageRequest groupMessageRequest) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (!groupMessageRequest.getFromUserId().equals(user.getId())) {
            throw new AccessDeniedException("You are not user send this group message");
        }

        MessageResponse messageResponse = messageService.storeGroupMessage(groupMessageRequest);

        return new ResponseEntity<>(messageResponse, HttpStatus.CREATED);
    }
}
