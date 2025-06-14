package payload;

public class SdpPayload implements WebRTCPayload {

    private String type;
    private String sdp;
    private Long fromUserId;
    private Long toUserId;


    public SdpPayload() {
    }

    public SdpPayload(String type, String sdp, Long fromUserId, Long toUserId) {
        this.type = type;
        this.sdp = sdp;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;

    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSdp() {
        return sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }

    public void setToUserId(Long toUserId) {
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

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }
}
