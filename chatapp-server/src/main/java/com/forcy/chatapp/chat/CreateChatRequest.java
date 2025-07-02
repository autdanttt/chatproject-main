package com.forcy.chatapp.chat;

import lombok.Data;
@Data
public class CreateChatRequest {
    private String email;
    private Long targetUserId;

}
