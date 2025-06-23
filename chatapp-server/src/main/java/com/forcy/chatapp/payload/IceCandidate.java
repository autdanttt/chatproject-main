package com.forcy.chatapp.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IceCandidate {
    private String sdpMid;
    private int sdpMLineIndex;
    private String candidate;
}
