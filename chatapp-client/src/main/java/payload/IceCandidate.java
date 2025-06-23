package payload;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IceCandidate {
    @JsonProperty("sdp_mid")
    private String sdpMid;
    @JsonProperty("sdp_m_line_index")
    private int sdpMLineIndex;
    @JsonProperty("candidate")
    private String candidate;

    public IceCandidate() {
    }

    public IceCandidate(String sdpMid, int sdpMLineIndex, String candidate) {
        this.sdpMid = sdpMid;
        this.sdpMLineIndex = sdpMLineIndex;
        this.candidate = candidate;
    }

    public String getSdpMid() {
        return sdpMid;
    }

    public void setSdpMid(String sdpMid) {
        this.sdpMid = sdpMid;
    }

    public int getSdpMLineIndex() {
        return sdpMLineIndex;
    }

    public void setSdpMLineIndex(int sdpMLineIndex) {
        this.sdpMLineIndex = sdpMLineIndex;
    }

    public String getCandidate() {
        return candidate;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }
}
