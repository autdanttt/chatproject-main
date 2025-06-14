package com.forcy.chatapp.message;

import com.forcy.chatapp.entity.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MessageRequest {

    private Long fromUserId;
    private Long toUserId;
    private MessageType messageType;
    private String content;
}
