package com.forcy.chatapp.chat;

import lombok.Data;
@Data
public class CreateChatRequest {
    private String email;
    private Long targetUserId;

//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }

}
