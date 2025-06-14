package com.forcy.chatapp.payload;

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
