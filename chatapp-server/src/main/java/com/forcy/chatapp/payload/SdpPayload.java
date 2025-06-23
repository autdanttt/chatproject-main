package com.forcy.chatapp.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SdpPayload {
    private String type;
    private String sdp;
    private Long fromUserId;
    private Long toUserId;

    @Override
    public String toString() {
        return "SdpPayload{" +
                "type='" + type + '\'' +
                ", sdp='" + sdp + '\'' +
                ", fromUserId=" + fromUserId +
                ", toUserId=" + toUserId +
                '}';
    }
}
