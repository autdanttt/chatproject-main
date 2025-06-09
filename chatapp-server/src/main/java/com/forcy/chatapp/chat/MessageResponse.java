package com.forcy.chatapp.chat;

import com.forcy.chatapp.entity.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;

}
