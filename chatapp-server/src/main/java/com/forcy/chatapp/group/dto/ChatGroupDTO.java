package com.forcy.chatapp.group.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChatGroupDTO {
    private Long id;
    private String name;
    private String image;
    private Long creatorId;
    private List<Long> memberIds;
}
