import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class MessageResponse {

    private Long messageId;
    private Long fromUserId;
    private Long toUserId;
    private MessageType messageType;
    private String content;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;

    public MessageResponse() {
    }

    public MessageResponse(Long messageId, Long fromUserId, Long toUserId, MessageType messageType, String content, LocalDateTime sentAt, LocalDateTime deliveredAt) {
        this.messageId = messageId;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.messageType = messageType;
        this.content = content;
        this.sentAt = sentAt;
        this.deliveredAt = deliveredAt;
    }

    public MessageResponse(String toUserId, String toUsername, String message) {
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "messageId=" + messageId +
                ", fromUserId=" + fromUserId +
                ", toUserId=" + toUserId +
                ", messageType=" + messageType +
                ", content='" + content + '\'' +
                ", sentAt=" + sentAt +
                ", deliveredAt=" + deliveredAt +
                '}';
    }
}