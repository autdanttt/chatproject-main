package com.forcy.chatapp.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IceCandidate {
    @JsonProperty("sdp_mid")
    private String sdpMid;
    @JsonProperty("sdp_m_line_index")
    private int sdpMLineIndex;
    @JsonProperty("candidate")
    private String candidate;
}
