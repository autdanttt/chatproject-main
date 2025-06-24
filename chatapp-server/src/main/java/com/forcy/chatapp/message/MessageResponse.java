package com.forcy.chatapp.message;

import com.forcy.chatapp.entity.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MessageResponse {
    private Long messageId;
    private Long fromUserId;
    private Long toUserId;
    private Long chatId;
    private MessageType messageType;
    private String content;
    private Date sentAt;
    private Date deliveredAt;

}
