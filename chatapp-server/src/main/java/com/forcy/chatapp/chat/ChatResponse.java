package com.forcy.chatapp.chat;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {
    private Long chatId;
    private Long otherUserId;
    private String otherUsername;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
}

