package com.forcy.chatapp.chat;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {
    private Long chatId;
    private Long otherUserId;
    private String otherUserFullName;
    private String imageUrl;
    private String lastMessage;
    private Date lastMessageTime;
}

