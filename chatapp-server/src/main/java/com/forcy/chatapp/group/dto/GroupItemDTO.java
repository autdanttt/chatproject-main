package com.forcy.chatapp.group.dto;

import com.forcy.chatapp.entity.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupItemDTO {

    private Long groupId;
    private String groupName;
    private String lastMessageContent;
    private String lastMessageSenderName;
    private Date lastMessageTime;

}
