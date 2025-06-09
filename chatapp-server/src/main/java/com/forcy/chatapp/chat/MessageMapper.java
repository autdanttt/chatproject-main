package com.forcy.chatapp.chat;

import com.forcy.chatapp.entity.Chat;
import com.forcy.chatapp.entity.Message;
import com.forcy.chatapp.entity.User;

import java.time.LocalDateTime;

public class MessageMapper {

     public static Message toEntity(MessageRequest request, User fromUser, Chat chat) {

        Message message = new Message();
        message.setUser(fromUser);
        message.setChat(chat);
        message.setType(request.getMessageType());
        message.setContent(request.getContent());
        message.setSentAt(LocalDateTime.now());

        return message;

    }

    public static MessageResponse toResponse(Message message, Long toUserId) {

        MessageResponse response = new MessageResponse();

        response.setMessageId(message.getId());
        response.setFromUserId(message.getUser().getId());
        response.setToUserId(toUserId);
        response.setChatId(message.getChat().getId());
        response.setMessageType(message.getType());
        response.setContent(message.getContent());
        response.setSentAt(message.getSentAt());
        response.setDeliveredAt(message.getDeliveredAt());


        return response;
    }

}
