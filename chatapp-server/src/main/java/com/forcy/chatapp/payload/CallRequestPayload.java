package com.forcy.chatapp.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CallRequestPayload {

    private Long toUserId;
    private Long fromUserId;
    private String callerName;
}
