package com.forcy.chatapp.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidatePayload {
    private String type;
    private IceCandidate candidate;
    private Long toUserId;
}
