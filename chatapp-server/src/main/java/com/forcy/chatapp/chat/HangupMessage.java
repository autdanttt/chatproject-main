package com.forcy.chatapp.chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HangupMessage {
    private String type; // = "hangup"
    private Long fromUserId;
    private Long toUserId;
}
