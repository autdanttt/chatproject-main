package payload;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CandidatePayload implements WebRTCPayload {
    private String type = "candidate";
    private IceCandidate candidate;
    @JsonProperty("to_user_id")
    private Long toUserId;

    public CandidatePayload() {
    }

    public CandidatePayload(IceCandidate candidate, Long toUserId) {
        this.candidate = candidate;
        this.toUserId = toUserId;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Long getToUserId() {
        return toUserId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public IceCandidate getCandidate() {
        return candidate;
    }

    public void setCandidate(IceCandidate candidate) {
        this.candidate = candidate;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }
}
