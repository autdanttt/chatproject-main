package com.forcy.chatapp.message;

import com.forcy.chatapp.entity.Chat;
import com.forcy.chatapp.entity.Message;
import com.forcy.chatapp.entity.User;

import java.time.LocalDateTime;
import java.util.Date;

public class MessageMapper {

     public static Message toEntity(MessageRequest request, User fromUser, Chat chat) {

        Message message = new Message();
        message.setUser(fromUser);
        message.setChat(chat);
        message.setType(request.getMessageType());
        message.setContent(request.getContent());
        message.setSentAt(new Date());

        return message;

    }

    public static MessageResponse toResponse(Message message, Long toUserId) {

        MessageResponse response = new MessageResponse();

        response.setMessageId(message.getId());
        response.setFromUserId(message.getUser().getId());
        response.setToUserId(toUserId);
        response.setChatId(message.getChat().getId());
        response.setGroupId(null);
        response.setMessageType(message.getType());
        response.setContent(message.getContent());
        response.setSentAt(new Date());
        response.setDeliveredAt(null);


        return response;
    }

}
