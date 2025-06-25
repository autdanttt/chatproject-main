package com.forcy.chatapp.message;

import com.forcy.chatapp.entity.MessageType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GroupMessageRequest {
    @NotNull
    private Long fromUserId;
    @NotNull
    private Long toGroupId;
    @NotNull
    private MessageType messageType;
    @NotNull
    private String content;
}
