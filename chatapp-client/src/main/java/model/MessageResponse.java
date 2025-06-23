package model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class MessageResponse {

    private Long messageId;      // Thêm để khớp với JSON

    private Long fromUserId;

    private Long toUserId;       // Thêm nếu cần (dùng để xác định chat 1-1)

    private Long chatId;

    private String messageType;  // Thêm nếu cần xử lý loại tin nhắn

    private String content;

    private Date sentAt;

    private Date deliveredAt;

    public Long getFromUserId() { return fromUserId; }
    public void setFromUserId(Long fromUserId) { this.fromUserId = fromUserId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Date getSentAt() { return sentAt; }
    public void setSentAt(Date sentAt) { this.sentAt = sentAt; }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Date getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Date deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    @Override
    public String toString() {
        return "MessageResponse{" +
                "content='" + content + '\'' +
                ", messageId=" + messageId +
                ", fromUserId=" + fromUserId +
                ", toUserId=" + toUserId +
                '}';
    }
}