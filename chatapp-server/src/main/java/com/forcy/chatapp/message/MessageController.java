package com.forcy.chatapp.message;

import com.forcy.chatapp.entity.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }


    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody MessageRequest messageRequest) {
        Message message = messageService.storeMessage(messageRequest);

        MessageResponse messageResponse = MessageMapper.toResponse(message, messageRequest.getToUserId());
        return new ResponseEntity<>(messageResponse, HttpStatus.CREATED);


    }
}
