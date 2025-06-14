package com.forcy.chatapp.payload;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SdpPayload {
    private String type;
    private String sdp;
    private Long toUserId;
    private Long fromUserId;

}
