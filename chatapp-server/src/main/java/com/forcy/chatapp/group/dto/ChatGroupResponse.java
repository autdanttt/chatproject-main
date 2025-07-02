package com.forcy.chatapp.group.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatGroupResponse {
    private Long groupId;
    private String groupName;
    private String imageUrl;
    private String lastMessageContent;
    private String lastMessageSenderName;
    private Date lastMessageTime;

}
